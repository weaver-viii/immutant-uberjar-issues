(defproject abc/abc "0.0.1-SNAPSHOT"
  :url          "https://github.com/instilled/"
  :description  "Classloader issues demonstration."

  :main         main
  :aot          [main]

  :manifest     {"Class-Path" "../mysql-connector-java-5.1.35-bin.jar"}
  :dependencies [[org.clojure/clojure        "1.6.0"]

                 [org.clojure/java.jdbc      "0.3.4"]
                 [com.mchange/c3p0           "0.9.2.1"]]

  :profiles {:mysql
             {:dependencies [[mysql/mysql-connector-java "5.1.31"]]}

             :immutant
             {:dependencies [[org.immutant/web "2.0.0"]]}}

  :jvm-opts          ["-Xmx1g" "-Djava.awt.headless=true"]
  :javac-options     ["-target" "1.7" "-source" "1.7"]

  ;; Default paths
  :source-paths      ["src/main/clojure"]
  :resource-paths    ["src/main/resources"]
  :test-paths        ["src/test/clojure"]
  :junit             ["src/test/java"]
  :java-source-paths ["src/main/java"
                      "src/test/java"]

  :compile-path      "target/classes"
  :target-path       "target/")
