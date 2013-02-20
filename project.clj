(defproject pokervis "0.1.0-SNAPSHOT"
  :description "The beginnings of a poker odds visualization program using Monte Carlo simulation"
  
  :url "https://github.com/muraiki/pokervis"
  
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.keminglabs/c2 "0.2.0-SNAPSHOT"]]
  
  :min-lein-version "2.0.0"
  
  :plugins [[lein-cljsbuild "0.3.0"]]
  
  :cljsbuild
   {:builds
    [{:source-paths ["src/cljs"],
      :compiler
      {:pretty-print true,
       :output-to "public/pokervis.js",
       :optimizations :whitespace}}]})
