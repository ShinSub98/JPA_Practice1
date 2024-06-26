package jpabook.jpashop.service.openApi.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeoResponse {
    private List<Document> documents;
    private Meta meta;

    @Data
    public static class Document {
        private Address address;
        private RoadAddress roadAddress;
        private String address_name;
        private String address_type;
        private String x;
        private String y;
    }

    @Data
    public static class Meta {
        private boolean is_end;
        private int pageableCount;
        private int totalCount;
    }

    @Data
    static class Address {

        private String address_name;

        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String region_4depth_h_name;

        private String h_code;
        private String b_code;
        private String mountain_yn;
        private String main_address_no;
        private String sub_address_no;

        private String x;
        private String y;
    }

    @Data
    static class RoadAddress {

        private String address_name;

        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;

        private String road_name;
        private String underground_yn;
        private String main_building_no;
        private String sub_building_no;
        private String building_name;
        private String zone_no;

        private String x;
        private String y;
    }
}
