package com.corona.apple.dao.repository;

import com.corona.apple.dao.model.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE name=:locationName AND isActive=1")
    Location getLocation(@Param("locationName") String locationName);
}
