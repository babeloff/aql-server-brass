(ns aql.brass.client
  (:require
   (org.httpkit [client :as clt])
   (ring.util [io :as ring-io])
   (clojure
    [string :as st]
    [pprint :as pp])
   (clojure.tools
     [logging :as log]
     [cli :as cli])
   (clojure.data [json :as json])
   (aql.brass [data :as brass-data])))

(def mutant-json-def
  {"martiServerModel"
   {"requirements"
    {"postgresqlPerturbation"
     {"tables"
      [{"table"  "cot_action"
        "columns"
         ["Event_Id"
          "Event_SourceId"
          "Event_How"
          "Event_ServerTime"
          "Position_PointCE"
          "Position_PointLE"
          "Position_TileX"
          "Position_Longitude"
          "Position_Latitude"]}
       {"table" "cot_detail"
        "columns"
        ["Position_Id"
         "Position_EventId"
         "Position_PointHae"
         "Event_Detail"
         "Position_TileY"
         "Event_CotType"]}]}}}})

(def mutant-json-live-1
  {"martiServerModel"
     {"requirements"
      {"postgresqlPerturbation"
       {"tables"
        [{"table" "ab9254b78c81c4303a2bac778a67343d8"
          "columns"
          ["source_id" "cot_type" "how" "detail" "servertime"]}
         {"table" "ae3962ebf18084c0f95329137329b88d8",
          "columns"
          ["point_hae" "point_ce" "point_le"
           "tileX" "tileY" "longitude" "latitude"]}]}}}})

(defn options [payload]
  {:method :post
   :headers {"content-type" "application/json; charset=UTF-8"}
   :body (->
          {:permutation payload}
          json/write-str
          ring-io/string-input-stream)})

(def cli-options
  [["-1" "--message-1" :id :m1]
   ["-2" "--message-2" :id :m2]])

(defn usage [options-summary]
  (->> ["Usage: program-name [options]"
        "Options:" options-summary]
       (st/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (st/join \newline errors)))

(defn validate-args
  [args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}

      errors
      {:exit-message (error-msg errors)}

      options
      {:options options :arguments arguments}

      :else
      {:exit-message (usage summary)})))

(defn process [message]
  (pp/pprint {:permutation message})
  (let [opts (options message)
        response (clt/post "http://localhost:9090/brass/p2/c1/json" opts)]
    (-> @response
        :body
        json/read-str
        pp/pprint)))

(defn run [args]
  (let [{:keys [options exit-message ok?]} (validate-args args)]
    (cond
      exit-message
      (do
        (println exit-message)
        (System/exit (if ok? 0 1)))

      (get options :m1) (process mutant-json-live-1)
      (get options :m2) (process mutant-json-def)
      :else (log/warn "options " options))))

(defn -main [& args] (run args))
