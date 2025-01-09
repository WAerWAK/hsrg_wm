package com.waerwak.entity;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
public class DishDto extends Dish{

    private String categoryName;
    private List<Flavor> flavors;

    public static class Flavor {
        private String name;
        private List<String> value;

        @JsonSetter("value")
        public void setValueFromJson(String valueJson) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            this.value = objectMapper.readValue(valueJson, List.class);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }

    }
}
