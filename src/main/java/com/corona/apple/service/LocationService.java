package com.corona.apple.service;

import java.util.Optional;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.repository.LocationRepository;
import com.corona.apple.service.mapper.MapperHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationService {

  @Autowired LocationRepository locationRepository;

  public Location getOrCreateLocation(String locationName) {
    Optional<Location> location = locationRepository.getLocation(locationName);
    return location.orElseGet(() -> locationRepository.save(MapperHelper.toLocation(locationName)));
  }

  public Location setLocation(Location location) {
    return locationRepository.save(location);
  }
}
