(defproject crud "0.0.1-SNAPSHOT"
  :description "Backend Service for crud - Build your backend from your frontend"
  :url "https://crud.gedankenessen.de/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.4.0"]
                 [com.novemberain/monger "3.6.0"]
                 [ring "1.9.4"]
                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-json "0.5.1"]
                 [ring-cors "0.1.13"]
                 [http-kit "2.5.3"]
                 [compojure "1.6.2"]
                 [buddy/buddy-sign "3.4.333"]
                 [buddy/buddy-hashers "1.8.158"]
                 [buddy/buddy-core "1.10.413"]]
  :main ^:skip-aot crud.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[ring/ring-devel "1.8.0"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
