package com.corona.apple.service;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.repository.LocationRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationService {

    @Autowired
    LocationRepository locationRepository;

    public Location getLocation(String locationName) {
        return locationRepository.getLocation(locationName);
    }

    public Location setLocation(Location location) {
        return locationRepository.save(location);
    }
}
