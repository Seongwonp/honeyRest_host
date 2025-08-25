package com.honeyrest.honeyrest_host.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import org.modelmapper.Converter;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean // 메서드로 빈이 들어옴.
    public ModelMapper modelMapper(ObjectMapper objectMapper) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE) // private 설정
                .setMatchingStrategy(MatchingStrategies.STRICT);// 엄격 근엄 진지

        Converter<String, JsonNode> toJsonNode = ctx -> {
            String s = ctx.getSource();
            try {
                if (s == null || s.isBlank()) return objectMapper.readTree("[]");
                return objectMapper.readTree(s);
            } catch (Exception e) {
                try { return objectMapper.readTree("[]"); } catch (Exception ignored) { return null; }
            }
        };

        // JsonNode -> String(JSON)
        Converter<JsonNode, String> toJsonString = ctx -> {
            JsonNode n = ctx.getSource();
            try {
                if (n == null || n.isNull()) return "[]";
                return objectMapper.writeValueAsString(n);
            } catch (Exception e) {
                return "[]";
            }
        };

        // 엔티티 -> DTO 매핑 규칙
        modelMapper.typeMap(Accommodation.class, AccommodationCreateRequestDTO.class).addMappings(m -> {
            // ID/이름 다른 필드
            m.map(Accommodation::getAccommodationId, AccommodationCreateRequestDTO::setAccommodationId);
            m.map(Accommodation::getThumbnail, AccommodationCreateRequestDTO::setThumbnailUrl);

            // 연관 ID (null-safe)
            m.<Long>map(src -> src.getCompany() == null ? null : src.getCompany().getCompanyId(),
                    AccommodationCreateRequestDTO::setCompanyId);
            m.<Long>map(src -> src.getCategory() == null ? null : src.getCategory().getCategoryId(),
                    AccommodationCreateRequestDTO::setCategoryId);
            m.<Long>map(src -> src.getMainRegion() == null ? null : src.getMainRegion().getRegionId(),
                    AccommodationCreateRequestDTO::setMainRegionId);
            m.<Long>map(src -> src.getSubRegion() == null ? null : src.getSubRegion().getRegionId(),
                    AccommodationCreateRequestDTO::setSubRegionId);

            // amenities: String → JsonNode
            m.using(toJsonNode).map(Accommodation::getAmenities, AccommodationCreateRequestDTO::setAmenities);
        });

        // DTO -> 엔티티 매핑 규칙  (주의: 연관관계는 ID만으로는 entity를 못 채우므로, 아래는 필드만)
        modelMapper.typeMap(AccommodationCreateRequestDTO.class, Accommodation.class).addMappings(m -> {

        });


        return modelMapper;
    }
}
