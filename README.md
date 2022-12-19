# ModularityChecker
This is a module separated from [WhoTouchedWhat](https://github.com/Jubilee101/WhoTouchedWhat).
ModularityChecker analyzes the overall dependency between given modules by
seeing how many changes are within a modules and how many crosses multiple modules.
<pr>

In order to use this checker, you can run `mvn install` to generate the corresponding jar file.
Then run `java -jar .\target\ModularityChecker-1.0-SNAPSHOT.jar .`. The argument taken here is the address
at which your `meta_data.json` locates. You can also specify other root directory.
<pr>

Inside `meta_data.json`, specify your repository address which you cloned locally, and the modules you want to analyze. The `modules` field
is a 2d array, each array inside is a module containing a series of files/folders which you consider belonging together.
All the address are separated by `'/'`.
<pr>

A sample of `meta_data.json` is provided [here](). After the checker finish analyzing, it generates a
`info.json` under the same directory which you specified in the argument, containing a total modularity `score`, 
which is the ratio of number of changes across modules (`extra`) to the number
of changes within one module (`intra`). A sample of `info.json` is provided [here]()