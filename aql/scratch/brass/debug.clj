
;; working with the p2 cp1 brass demo

(require '[aql.serialize :as ser] :reload)
(require '[aql.brass.server :as abs] :reload)

; (dbg/break ser/aql-format)
; (dbg/break-catch ser/aql-format)

(abs/-main)
