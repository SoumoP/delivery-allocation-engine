package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.config.Tile38Config;
import com.personal.delivery_allocation_engine.dao.PartnerDao;
import com.personal.delivery_allocation_engine.dto.partner.PartnerLocationInfo;
import com.personal.delivery_allocation_engine.dto.request.LocationUpdateRequest;
import com.personal.delivery_allocation_engine.dto.response.PartnerLocationResponse;
import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import com.personal.delivery_allocation_engine.utils.PartnerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Slf4j
@Service
public class PartnerLocationService {

  public static final String PARTNER_KEY = "partner:";
  private final RedisTemplate<String, Object> tile38Template;
  private final Tile38Config tile38Config;
  private static final String PARTNERS_COLLECTION = "delivery_partners";
  private final PartnerDao partnerDao;

  public PartnerLocationService(@Qualifier("tile38RedisTemplate") RedisTemplate<String, Object> tile38Template,
      Tile38Config tile38Config, PartnerDao partnerDao) {
    this.tile38Template = tile38Template;
    this.tile38Config = tile38Config;
    this.partnerDao = partnerDao;
  }

  public PartnerLocationResponse updatePartnerLocation(Long partnerId, LocationUpdateRequest request) {
    String key = PARTNER_KEY + partnerId;
    Double lat = request.getLatitude();
    Double lng = request.getLongitude();

    // Fetch partner status (if not already available in context)
    Partner partner = partnerDao.getPartner(partnerId);

    tile38Template.execute((RedisCallback<Object>) connection -> {
      connection.execute("SET", PARTNERS_COLLECTION.getBytes(), key.getBytes(), "POINT".getBytes(),
          String.valueOf(lat).getBytes(), String.valueOf(lng).getBytes(), "FIELD".getBytes(), "status".getBytes(),
          partner.getStatus().name().getBytes(), "FIELD".getBytes(), "updated_at".getBytes(),
          String.valueOf(System.currentTimeMillis()).getBytes());
      return null;
    });

    log.info("Updated location and metadata for partner {} at [{}, {}]", partnerId, lat, lng);
    return PartnerLocationResponse.builder().id(partnerId).name(partner.getName())
        .currentLocation(String.format("%f,%f", lng, lat)).status(partner.getStatus().name()).build();
  }

  public List<PartnerLocationInfo> findPartnersNearRestaurant(double lat, double lng, double radiusKm) {
    try {
      List<Object> result = tile38Template.execute((RedisCallback<List<Object>>) connection -> {
        Object rawResult = connection.execute("NEARBY", PARTNERS_COLLECTION.getBytes(), "POINT".getBytes(),
            String.valueOf(lat).getBytes(), String.valueOf(lng).getBytes(), String.valueOf(radiusKm * 1000).getBytes(),
            "LIMIT".getBytes(), String.valueOf(500).getBytes(), "WITHFIELDS".getBytes());
        return (List<Object>) rawResult;
      });

      List<PartnerLocationInfo> parsed = parseNearbyResult(result);
      return parsed.stream().filter(p -> p.getStatus() == PartnerStatus.AVAILABLE).collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Failed to find partners near location [{}, {}]: {}", lat, lng, e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  // Fixed getPartnerLocation method
  public Optional<PartnerLocationInfo> getPartnerLocation(Long partnerId) {
    try {
      String key = PARTNER_KEY + partnerId;

      Object result = tile38Template.execute((RedisCallback<Object>) connection -> {
        return connection.execute("GET", PARTNERS_COLLECTION.getBytes(), key.getBytes(), "WITHFIELDS".getBytes());
      });

      return parseGetResult(partnerId, result);
    } catch (Exception e) {
      log.error("Failed to get location for partner {}: {}", partnerId, e.getMessage(), e);
      return Optional.empty();
    }
  }

  private Optional<PartnerLocationInfo> parseGetResult(Long partnerId, Object result) {
    try {
      if (result instanceof List) {
        List<Object> data = (List<Object>) result;
        if (data.size() >= 2 && data.get(1) instanceof List) {
          List<Object> coords = (List<Object>) data.get(1);
          if (coords.size() >= 2) {
            double lng = Double.parseDouble(new String((byte[]) coords.get(0))); // Convert byte array
            double lat = Double.parseDouble(new String((byte[]) coords.get(1))); // Convert byte array

            return Optional.of(
                new PartnerLocationInfo(partnerId, lat, lng, 0.0, PartnerStatus.AVAILABLE, LocalDateTime.now()));
          }
        }
      }
    } catch (Exception e) {
      log.warn("Failed to parse get result for partner {}: {}", partnerId, e.getMessage());
    }

    return Optional.empty();
  }

  private List<PartnerLocationInfo> parseNearbyResult(List<Object> result) {
    List<PartnerLocationInfo> partners = new ArrayList<>();

    if (result != null && !result.isEmpty()) {
      try {
        if (result.size() > 1 && result.get(1) instanceof List) {
          List<Object> items = (List<Object>) result.get(1);

          for (Object item : items) {
            if (item instanceof List) {
              List<Object> partnerData = (List<Object>) item;
              if (partnerData.size() >= 3) {
                String key = new String((byte[]) partnerData.get(0)); // Convert byte array
                Long partnerId = PartnerUtils.extractPartnerIdFromKey(key);

                if (partnerData.get(1) instanceof List) {
                  List<Object> coords = (List<Object>) partnerData.get(1);
                  double lng = Double.parseDouble(new String((byte[]) coords.get(0))); // Convert byte array
                  double lat = Double.parseDouble(new String((byte[]) coords.get(1))); // Convert byte array

                  double distance = Double.parseDouble(new String((byte[]) partnerData.get(2))); // Convert byte array

                  partners.add(new PartnerLocationInfo(partnerId, lat, lng, distance, PartnerStatus.AVAILABLE,
                      LocalDateTime.now()));
                }
              }
            }
          }
        }
      } catch (Exception e) {
        log.warn("Failed to parse nearby result: {}", e.getMessage());
      }
    }

    return partners;
  }

  public void removePartnerLocation(Long partnerId) {
    try {
      String key = PARTNER_KEY + partnerId;

      tile38Template.execute((RedisCallback<Object>) connection -> {
        connection.execute("DEL", PARTNERS_COLLECTION.getBytes(), key.getBytes());
        return null;
      });

      log.debug("Removed partner {} from location tracking", partnerId);
    } catch (Exception e) {
      log.error("Failed to remove partner {} from location tracking: {}", partnerId, e.getMessage(), e);
    }
  }

  private void setPartnerMetadata(String key, PartnerStatus status) {
    try {
      tile38Template.execute((RedisCallback<Object>) connection -> {
        connection.execute("FSET", PARTNERS_COLLECTION.getBytes(), key.getBytes(), "status".getBytes(),
            status.name().getBytes(), "updated_at".getBytes(), String.valueOf(System.currentTimeMillis()).getBytes());
        return null;
      });
    } catch (Exception e) {
      log.warn("Failed to set metadata for partner {}: {}", key, e.getMessage());
    }
  }

}
