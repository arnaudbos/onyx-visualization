(ns onyx-viz.core-cards
  (:require [om.core :as om :include-macros true]
            [onyx-viz.core :as c]
            [sablono.core :as sab :include-macros true])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-om deftest]]))

(enable-console-print!)

(def job
  {:catalog [{:bookkeeper/password-bytes "password"
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
	      :bookkeeper/quorum-size 3}
	     {:onyx/name :annotate-job,
	      :onyx/fn :onyx-peers.functions.functions/annotate-job-num,
	      :jepsen/job-num 4,
	      :onyx/params [:jepsen/job-num],
	      :onyx/type :function,
	      :onyx/batch-size 1}
             {:bookkeeper/password-bytes "password"
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/name :read-ledger-6,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 6,
              :onyx/batch-size 1}
             {:bookkeeper/password-bytes "password"
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/name :read-ledger-5,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 6,
              :onyx/batch-size 1}
             {:bookkeeper/password-bytes "password"
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/name :read-ledger-4,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 6,
              :onyx/batch-size 1}
             {:bookkeeper/password-bytes "password"
              :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
              :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
              :onyx/medium :bookkeeper,
              :onyx/type :input,
              :onyx/name :read-ledger-3,
              :onyx/max-pending 5000,
              :onyx/max-peers 1,
              :bookkeeper/no-recovery? true,
              :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
              :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
              :onyx/doc "Reads a sequence from a BookKeeper ledger",
              :bookkeeper/digest-type :mac,
              :onyx/pending-timeout 10000,
              :bookkeeper/ledger-id 6,
              :onyx/batch-size 1}
	     {:bookkeeper/password-bytes "password"
	      :onyx/plugin :onyx.plugin.bookkeeper/read-ledgers,
	      :bookkeeper/deserializer-fn :onyx.compression.nippy/zookeeper-decompress,
	      :onyx/medium :bookkeeper,
	      :onyx/type :input,
	      :onyx/name :read-ledger-7,
	      :onyx/max-pending 5000,
	      :onyx/max-peers 1,
	      :bookkeeper/no-recovery? true,
	      :bookkeeper/zookeeper-addr "n1:2181,n2:2181,n3:2181,n4:2181,n5:2181",
	      :bookkeeper/zookeeper-ledgers-root-path "/onyx/JEPSENONYXID/ledgers",
	      :onyx/doc "Reads a sequence from a BookKeeper ledger",
	      :bookkeeper/digest-type :mac,
	      :onyx/pending-timeout 10000,
	      :bookkeeper/ledger-id 7,
	      :onyx/batch-size 1}],
   :lifecycles [{:lifecycle/task :all,
		 :lifecycle/calls :onyx.lifecycle.metrics.metrics/calls,
		 :metrics/buffer-capacity 10000,
		 :metrics/workflow-name "simple-job",
		 :metrics/sender-fn :onyx.lifecycle.metrics.timbre/timbre-sender,
		 :lifecycle/doc "Instruments a task's metrics to timbre"}
		{:lifecycle/task :all,
		 :lifecycle/calls :onyx-peers.lifecycles.restart-lifecycle/restart-calls}
		{:lifecycle/task :persist,
		 :lifecycle/calls :onyx.plugin.bookkeeper/write-ledger-calls}
		{:lifecycle/task :read-ledger-7,
		 :lifecycle/calls :onyx.plugin.bookkeeper/read-ledgers-calls}],
   :workflow [[:annotate-job :persist] 
              [:read-ledger-3 :annotate-job]
              [:read-ledger-4 :annotate-job]
              [:read-ledger-5 :annotate-job]
              [:read-ledger-6 :annotate-job]
              [:read-ledger-7 :annotate-job]],
   :task-scheduler :onyx.task-scheduler/balanced})
        
(defcard-om first-card
  c/job-dag
  {:job job :width 900 :height 480})

(def app-state (atom {:job job :width 900 :height 480}))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (.log js/console "main"
        (.getElementById js/document "main-app-area")
        )
  (if-let [node (.getElementById js/document "main-app-area")]
    (do (.log js/console "main-app-area")
        (om/root c/job-dag app-state {:target node}))))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

