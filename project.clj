(defproject basic-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring "1.8.2"]
                 [compojure "1.6.2"]]
  :plugins [[lein-cljfmt "0.7.0"]]
  :main ^:skip-aot basic-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
