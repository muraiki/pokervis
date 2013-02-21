# pokervis

The beginnings of a poker odds visualization program in Clojurescript using Monte Carlo simulation. C2 is used for image visualization. It currently only works in Chrome, but I'm working on that!

# How to use

First install [Leiningen](http://leiningen.org/). Then clone the repo and run:
```
lein deps
lein cljsbuild once
```

This will generate two js files in /public: pokervis.js is fully optimized but not really suitable for debugging. pokervis-dev.js is much larger and slower, but it's easier to map JS errors in it to the original clojurescript source.

# How to develop

If you are working on this, run `lein cljsbuild auto dev` to automatically recompile the js whenever a source file changes. Change dev to prod to generate the production js, or leave it off completely to keep generating both builds.

I'm new to Clojure and Clojurescript, so please excuse any bad code! I'm also working on turning the poker engine into its own library, [clojurepoker](https://github.com/muraiki/clojurepoker)

## License

Copyright © 2013 Erik Ferguson and Chip Hogg.

Distributed under the Eclipse Public License, the same as Clojure.