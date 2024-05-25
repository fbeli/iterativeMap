package com.becb.processnewpoint.dto;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;



    @ExtendWith(MockitoExtension.class)
    class PointDtoTest {

        @Test
        void testPointDtoConstructor() {
            // create a Point object with some sample values
            Point point = new Point();
            point.setPointId("AAAAAAA");
            point.setTitle("Test Point");
            point.setDescription("This is a test point.");
            point.setLatitude("37.7749");
            point.setLongitude("-122.4194");
            point.setCity("San Francisco");
            point.setState("CA");
            point.setCountry("USA");
            point.setCreateTime(LocalDateTime.now());
            User user = new User();
            user.setUserId("XXXXX");
            point.setUser(user);

            Point pChild = new Point();
            pChild.setPointId("BBBBB");
            point.addChildPoint(pChild);

            PointDto pointDto = new PointDto(point);
            assertEquals(point.getId(), pointDto.getId());
            assertEquals(point.getTitle(), pointDto.getTitle());
            assertEquals(point.getDescription(), pointDto.getDescription());
            assertEquals(point.getLatitude(), pointDto.getLatitude());
            assertEquals(point.getLongitude(), pointDto.getLongitude());
            assertEquals(point.getCity(), pointDto.getCity());
            assertEquals(point.getState(), pointDto.getState());
            assertEquals(point.getCountry(), pointDto.getCountry());
            assertEquals(point.getCreateTime(), pointDto.getCreateTime());
            assertEquals(1, pointDto.getChildrenPoints().size());

        }

}