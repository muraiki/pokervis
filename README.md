# pokervis

The beginnings of a poker odds visualization program in Clojurescript using Monte Carlo simulation. C2 is used for image visualization. It currently only works in Chrome, but I'm working on that!

# How to use

First install [Leiningen](http://leiningen.org/)
Clone the repo and run:
```
lein deps
lein cljsbuild once
```

This will generate two js files in /public, the default pokervis.js is not really optimized, but is easier to debug. The pokervis-prod.js is optimized, is about 10x smaller, and runs much faster.

# How to develop

If you are working on this, run `lein cljsbuild auto dev` to automatically recompile the js whenever a source file changes. Change dev to prod to generate the production js, or leave it off completely to keep generating both builds.

There's still some stuff left over in this project from the initial version which was in Clojure, which I'll clean out. I'd like to bring my tests over to Clojurescript, though. And I'd also like to make the poker engine a proper library!

## License

Copyright © 2013 Erik Ferguson and Chip Hogg.

Distributed under the Eclipse Public License, the same as Clojure.