package com.corona.apple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface CommonUtils {

  double popularityTimeFactor = 1.0 / 45000;

  String POPULARITY_FORMULA_REFERENCE_DATE = "Jan 1 2005";

  static long millisTillDate(String date) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
    return df.parse(date).getTime();
  }

  static void feedProductAccessCount(Product product) throws ParseException {
    product.setAccessCount(product.getAccessCount()+1);
    product.setPopularity(CommonUtils.getPopularity(product.getViews(), product.getAccessCount(), product.getCreatedAt()));
  }

  static void feedProductView(Product product) throws ParseException {
    product.setViews(product.getViews()+1);
    product.setPopularity(CommonUtils.getPopularity(product.getViews(), product.getAccessCount(), product.getCreatedAt()));
  }

  static void feedTagAccessCount(Tag tag) throws ParseException {
    tag.setAccessCount(tag.getAccessCount()+1);
    tag.setPopularity(CommonUtils.getPopularity(tag.getViews(), tag.getAccessCount(), tag.getCreatedAt()));
  }

  static void feedTagView(Tag tag) throws ParseException {
    tag.setViews(tag.getViews()+1);
    tag.setPopularity(CommonUtils.getPopularity(tag.getViews(), tag.getAccessCount(), tag.getCreatedAt()));
  }

  static double getPopularity(long views, long accessCount, Date createdAt) throws ParseException {

    long timeDiff =
        ((createdAt.getTime()
                - CommonUtils.millisTillDate(CommonUtils.POPULARITY_FORMULA_REFERENCE_DATE))
            / 1000);

    return Math.log10(views + accessCount) + timeDiff * CommonUtils.popularityTimeFactor;
  }

  static Pageable getDefaultPaginationObject(final Integer from, final Integer size) {
    return PageRequest.of(from / size, size, Sort.by("createdAt").descending());
  }
}
