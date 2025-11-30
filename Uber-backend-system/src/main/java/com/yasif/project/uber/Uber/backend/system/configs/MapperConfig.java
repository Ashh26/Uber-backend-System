package com.yasif.project.uber.Uber.backend.system.configs;

import com.yasif.project.uber.Uber.backend.system.dto.PointDto;
import com.yasif.project.uber.Uber.backend.system.utils.GeometryUtil;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(PointDto.class, Point.class).setConverter(context ->{
            PointDto pointDto = context.getSource();
            return GeometryUtil.createPoint(pointDto);
        });

        modelMapper.typeMap(Point.class,PointDto.class).setConverter(context ->{
            Point point = context.getSource();
            Double coordinates[] = {
                    point.getX(),
                    point.getY()
            };

            return new PointDto(coordinates);
        });


        return modelMapper;
    }

}
