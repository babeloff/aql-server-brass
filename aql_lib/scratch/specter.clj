(require '(com.rpl [specter :as sr]))
(require '(aql [wrap :as aw]) :reload)

(def MY-PATH (sr/path even?))
(sr/select [sr/ALL MY-PATH] (range 10))

(def reqs {:query ["Qs_01" "Qt_01" "Qs_02" "Qt_02"]})
(sr/declarepath IS-QUERY)
(sr/providepath IS-QUERY (sr/cond-path (sr/must "query") "query" (sr/must :query) :query))
(sr/select [IS-QUERY] reqs)
(sr/select [(sr/cond-path (sr/must "query") "query" (sr/must :query) :query)] reqs)
