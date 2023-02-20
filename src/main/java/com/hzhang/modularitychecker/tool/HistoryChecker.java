package com.hzhang.modularitychecker.tool;

import com.hzhang.modularitychecker.model.Info;
import com.hzhang.modularitychecker.model.MetaData;
import com.hzhang.modularitychecker.model.Pair;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryChecker {
    private final Repository repository;
    private final MetaData metaData;
    private Map<String, Integer> fileModule;
    private Map<String, List<String>> commitFiles;
    private Map<Pair, Integer> dependencies;
    private List<Map<String, Integer>> moduleAuthorsCounts;
    private List<Long> moduleLines;
    private long intra;
    private long extra;
    private double score;
    public HistoryChecker(Repository repo, MetaData metaData) {
        repository = repo;
        this.metaData = metaData;
        fileModule = new HashMap<>();
        commitFiles = new HashMap<>();
        dependencies = new HashMap<>();
        moduleAuthorsCounts = new ArrayList<>();
        moduleLines = new ArrayList<>();
        for (int i = 0; i < metaData.modules.size(); i++) {
            moduleAuthorsCounts.add(new HashMap<>());
            moduleLines.add(0L);
        }
    }
    public void checkHistory(String filePath, String repoAddress) {
        int moduleIndex = searchForModule(filePath);
        if (moduleIndex < 0) {
//            System.out.println("no module found for file: " + filePath);
            return;
        }
        int lines = LineCounter.countLines(repoAddress + '/' + filePath);
        moduleLines.set(moduleIndex, moduleLines.get(moduleIndex) + lines);
        fileModule.put(filePath, moduleIndex);
        RevWalk rw = LogFollowCommand.follow(repository, filePath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        Set<String> paths = new HashSet<>();
        paths.add(filePath);
        try {
            // traverse all the commits related to this file
            for (RevCommit commit : rw) {
                addCommitFiles(commit, filePath);
                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs;
                if (commit.getParentCount() == 0) {
                    AbstractTreeIterator oldTreeIter = new EmptyTreeIterator();
                    ObjectReader reader = repository.newObjectReader();
                    AbstractTreeIterator newTreeIter = new CanonicalTreeParser(null, reader, commit.getTree());
                    diffs = df.scan(oldTreeIter, newTreeIter);
                } else {
                    diffs = df.scan(commit.getParent(0).getTree(), commit.getTree());
                }

                for (DiffEntry diff : diffs) {
                    String newPath = diff.getNewPath();
                    String oldPath = diff.getOldPath();
                    if (paths.contains(newPath)) {
                        if (!oldPath.equals("/dev/null")){
                            paths.add(oldPath);
                        }
                    } else if (newPath.equals("/dev/null") && paths.contains(oldPath)) {
                        // do nothing
                    }
                    else {
                        continue;
                    }
                    int count = 0;
                    for (Edit edit : df.toFileHeader(diff).toEditList()) {
                        count += edit.getEndA() - edit.getBeginA();
                        count += edit.getEndB() - edit.getBeginB();
                    }
                    if (count == 0) {
                        count = 1;
                    }
                    String author = commit.getAuthorIdent().getName();
                    Map<String, Integer> authorCount = moduleAuthorsCounts.get(moduleIndex);
                    authorCount.put(author, authorCount.getOrDefault(author, 0) + count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addCommitFiles(RevCommit commit, String filePath) {
        String commitName = commit.toObjectId().getName();
        List files = commitFiles.get(commitName);
        if (files == null) {
            files = new ArrayList<>();
        }
        files.add(filePath);
        commitFiles.put(commitName, files);
    }
    // return module index if found, else return -1
    private int searchForModule(String filePath) {
        List<List<String>> modules = metaData.modules;
        for (int i = 0; i < modules.size(); i++) {
            List<String> module = modules.get(i);
            for (String prefix : module) {
                if (filePath.startsWith(prefix)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Info summarize() {
        intra = 0;
        extra = 0;
        for (List<String> files : commitFiles.values()) {
            for (int i = 0; i < files.size(); i++) {
                for (int j = 0; j < files.size(); j++) {
                    String file1 = files.get(i);
                    String file2 = files.get(j);
                    Integer module1 = fileModule.get(file1);
                    Integer module2 = fileModule.get(file2);
                    Pair pair = new Pair(file1, file2, module1, module2);
                    dependencies.put(pair, dependencies.getOrDefault(pair, 0) + 1);
                }
            }
        }
        for (Map.Entry<Pair, Integer> entry : dependencies.entrySet()) {
            Pair pair = entry.getKey();
            int count = entry.getValue();
            if (pair.sameModule()) {
                intra += count;
            } else {
                extra += count;
            }
        }
        score = (double) extra / intra;
        Info info = new Info(score, intra, extra, moduleLines, moduleAuthorsCounts);
        return info;
    }

    public double getScore() {
        return score;
    }
}
