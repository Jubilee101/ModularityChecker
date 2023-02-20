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
    @JsonProperty("module_lines")
    private List<Long> moduleLines;
    @JsonProperty("module_author")
    private List<Map<String, Integer>> moduleAuthorsCounts;
    @JsonProperty("baseline")
    private double baseline;

    public Info(){}
    public Info(double score, long intra, long extra, List<Long> lines, List<Map<String, Integer>> moduleAuthorsCounts) {
        this.score = score;
        this.intra = intra;
        this.extra = extra;
        this.moduleLines = lines;
        this.moduleAuthorsCounts = moduleAuthorsCounts;
        // currently, only support baseline for 2 modules
        if (moduleLines.size() != 2) {
            baseline = 0;
        } else {
            long l1 = moduleLines.get(0);
            long l2 = moduleLines.get(1);
            baseline = ((double) (2 * l1 * l2)) / ((l1 + l2 - 1) * (l1 + l2));
            baseline = baseline / (1 - baseline);
        }
    }
}
