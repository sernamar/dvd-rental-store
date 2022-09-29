(ns build
  (:require [clojure.tools.build.api :as b]
            [clojure.java.io :as io]))

(def source-dir "src/")
(def target-dir "target/")
(def class-dir (str target-dir "classes/"))
(def basis (b/create-basis {:project "deps.edn"}))

(def uber-file (str target-dir "dvd-rental-store.jar"))

(defn- ensure-dir [dir]
  (let [dir (io/file dir)]
    (when-not (.exists dir)
      (.mkdirs dir))))

(defn clean [_]
  (b/delete {:path target-dir}))

(defn uberjar [_]
  (clean nil)
  (ensure-dir target-dir)
  (b/copy-dir {:src-dirs [source-dir]
               :target-dir target-dir})
  (b/compile-clj {:basis basis
                  :src-dirs [source-dir]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'dvd-rental-store.server}))
