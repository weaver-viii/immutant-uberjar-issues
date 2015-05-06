(ns main
  (:gen-class)
  (:require
    [clojure.java.jdbc     :as jdbc])
  (:import
    [com.mchange.v2.c3p0 ComboPooledDataSource]
    [java.sql Timestamp]))

(defn make-connection
  [uri]
  (let [strip-jdbc #'jdbc/strip-jdbc
        parse-uri #'jdbc/parse-properties-uri]
    (-> uri
        (strip-jdbc)
        (java.net.URI.)
        (parse-uri)
        (assoc :useLegacyDatetimeCode false))))

(defn make-pooled-connection
  "Create a pooled data source from a connection. Use make-connection
   function to create an appropriate structure."
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    (merge spec {:datasource cpds})))

(defn -main
  [& args]
  (let [conn (-> "jdbc:mysql://localhost:3306/test?user=root"
                 (make-connection)
                 (make-pooled-connection))]
    (println)
    (println)
    (println "#########################################################")
    (println)
    (println "Immutant uberjar classpath issue demonstration.")
    (println)
    (println "mysql-jdbc.jar is referenced by the Manifest's")
    (println "Class-Path property.")
    (println)
    (println "--------------------------------------------------------")
    (println "CASE 1: working -> no immutant, no mysql in uberjar")
    (println)
    (println "lein uberjar \\")
    (println " && java -jar target/abc-0.0.1-SNAPSHOT-standalone.jar")
    (println)
    (println "This should print an exception about a missing database")
    (println "or the like and NOT 'no suitable driver found'!")
    (println)
    (println "--------------------------------------------------------")
    (println "CASE 2: working -> immutant, with mysql dep in uberjar")
    (println)
    (println "lein with-profiles immutant,mysql uberjar \\")
    (println " && java -jar target/abc-0.0.1-SNAPSHOT-standalone.jar")
    (println)
    (println "This should print an exception about a missing database")
    (println "or the like and NOT 'no suitable driver found'!")
    (println )
    (println "--------------------------------------------------------")
    (println "CASE 3: broken! -> immutant, WITHOU mysql dep in uberjar")
    (println)
    (println "lein with-profiles immutant uberjar \\")
    (println " && java -jar target/abc-0.0.1-SNAPSHOT-standalone.jar")
    (println)
    (println "This will print exception that no suitable driver could")
    (println "could be found! What am I doing wrong?")
    (println)
    (println "#########################################################")
    (println)
    (jdbc/query conn "SELECT * FROM dont_care")))
