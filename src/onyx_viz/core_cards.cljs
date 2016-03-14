(ns onyx-viz.core-cards
  (:require [om.core :as om :include-macros true]
            [onyx-viz.core :as c]
            [sablono.core :as sab :include-macros true])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-om deftest]]))

(enable-console-print!)

(def job
  {:workflow [[:read-ledger-3 :unwrap]
              [:read-ledger-4 :unwrap]
              [:read-ledger-5 :unwrap]
              [:read-ledger-6 :unwrap]
              [:read-ledger-7 :unwrap]
              [:unwrap :annotate-job]
              [:annotate-job :persist]],
   :task-scheduler :onyx.task-scheduler/balanced
   :catalog [{:onyx/name :read-ledger-3,
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/password-bytes "password"
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 3,
              :onyx/batch-size 20}
             {:onyx/name :read-ledger-4,
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/password-bytes "password"
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 3,
              :onyx/batch-size 20}
             {:onyx/name :read-ledger-5,
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/password-bytes "password"
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 3,
              :onyx/batch-size 20}
             {:onyx/name :read-ledger-6,
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/password-bytes "password"
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 3,
              :onyx/batch-size 20}
             {:onyx/name :read-ledger-7,
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/password-bytes "password"
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 3,
              :onyx/batch-size 20}
             {:onyx/name :unwrap,
              :onyx/fn :onyx-peers.functions.functions/unwrap,
              :onyx/type :function,
              :onyx/batch-size 20}
             {:onyx/name :annotate-job,
              :onyx/fn :onyx-peers.functions.functions/annotate-job-num,
              :onyx/uniqueness-key :id,
              :onyx/n-peers 1,
              :jepsen/job-num 0,
              :onyx/params [:jepsen/job-num],
              :onyx/type :function,
              :onyx/batch-size 20}
             {:bookkeeper/password-bytes "password"
              :onyx/plugin :onyx.plugin.bookkeeper/write-ledger,
              :onyx/medium :bookkeeper,
              :onyx/type :output,
              :onyx/name :persist,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/ensemble-size 3,
              :onyx/doc "Writes messages to a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :bookkeeper/serializer-fn :onyx.compression.nippy/zookeeper-compress,
              :onyx/batch-size 1,
              :bookkeeper/quorum-size 3}]
   :windows [{:window/id :collect-segments,
              :window/task :annotate-job,
              :window/type :global,
              :window/aggregation :onyx.windowing.aggregation/conj,
              :window/window-key :event-time}],
   :triggers [{:trigger/window-id :collect-segments,
               :trigger/refinement :onyx.triggers.refinements/accumulating,
               :trigger/on :onyx.triggers.triggers/segment,
               :trigger/threshold [1 :elements],
               :trigger/sync :onyx-peers.functions.functions/update-state-log}],
   :lifecycles [(comment ... lifecycles elided for brevity)]})
        
(defcard-om first-card
  c/job-dag
  {:job job :width 960 :height 480})

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

