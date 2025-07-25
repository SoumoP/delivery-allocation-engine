package com.personal.delivery_allocation_engine.utils;

import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@UtilityClass
public class PartnerUtils {
  public boolean isPartnerAvailable(Partner partner) {
    return Objects.nonNull(partner) && partner.getStatus() == PartnerStatus.AVAILABLE;
  }

  public Long extractPartnerIdFromKey(String key) {
    return Long.parseLong(key.replace("partner:", ""));
  }
}
