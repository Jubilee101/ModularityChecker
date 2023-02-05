package com.hzhang.modularitychecker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Info {
    @JsonProperty("score")
    private double score;
    @JsonProperty("intra")
    private long intra;
    @JsonProperty("extra")
    private long extra;
    @JsonProperty("module_author")
    private List<Map<String, Integer>> moduleAuthorsCounts;

    public Info(){}
    public Info(double score, long intra, long extra, List<Map<String, Integer>> moduleAuthorsCounts) {
        this.score = score;
        this.intra = intra;
        this.extra = extra;
        this.moduleAuthorsCounts = moduleAuthorsCounts;
    }
}
