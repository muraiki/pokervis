# pokervis

The beginnings of a poker odds visualization program in Clojurescript using Monte Carlo simulation. Currently, it continually generates 7 card hands, takes the best 5 card hand, and plots a dynamically updated bar chart of each hand's frequency relative to the others.

C2 is used for image visualization. It should work in Chrome, Firefox, IE 10, and Safari 6 or greater.

# Demonstration

You can view the program in action [here](http://muraiki.com/pokervis/)

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

Copyright © 2013 Erik Ferguson and Chip Hogg. All rights reserved.
The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
which can be found in the file epl-v10.html at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license. You must not remove this notice, or any other, from this software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.