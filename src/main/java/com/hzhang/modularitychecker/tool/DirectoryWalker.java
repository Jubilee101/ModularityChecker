package com.hzhang.modularitychecker.tool;
import com.hzhang.modularitychecker.model.Info;
import com.hzhang.modularitychecker.model.MetaData;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DirectoryWalker {
    private Repository repository;
    private MetaData metaData;
    private HistoryChecker checker;
    public DirectoryWalker(MetaData input) {
        metaData = input;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            String gitAddr = Paths.get(metaData.repoAddress, ".git").toString();
            repository = builder.setGitDir(new File(gitAddr))
                    .readEnvironment()
                    .findGitDir()
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checker = new HistoryChecker(repository, metaData);
    }

    public Info walk() throws IOException {
        Ref head = repository.findRef("HEAD");
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(head.getObjectId());
        RevTree tree = commit.getTree();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(false);
        treeWalk.setPostOrderTraversal(true);
        walkDirectoryTree(treeWalk, head.getName());
        return checker.summarize();
    }

    public void walkDirectoryTree(TreeWalk treeWalk, String directory) throws IOException {
        while (treeWalk.next()) {
            if (treeWalk.getNameString().equals(directory)) {
                return;
            }
            boolean isLeaf = true;
            String path = treeWalk.getPathString();
            if (treeWalk.isSubtree()) {
                isLeaf = false;
                // if belongs to one module, add authors of the file to that module
                // do something about the dependency
                treeWalk.enterSubtree();
                walkDirectoryTree(treeWalk, treeWalk.getNameString());
            }
            if (isLeaf) {
                checker.checkHistory(path);
            }
        }
    }
}