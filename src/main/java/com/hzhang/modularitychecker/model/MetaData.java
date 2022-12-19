package com.hzhang.modularitychecker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MetaData {
    @JsonProperty("address")
    public String repoAddress;
    @JsonProperty("modules")
    public List<List<String>> modules;
    public MetaData(){}
}
