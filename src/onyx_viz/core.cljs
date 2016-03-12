(ns onyx-viz.core
  (:require [om.core :as om :include-macros true]
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
        
(def Graph (.-Graph (.-graphlib js/dagreD3)))
        
(defn task-class [task]
  (case (:onyx/type task)
    :input "type-input"
    :function "type-function"
    :output "type-output"))

(defn style-tooltip [task-name->entry name]
  (.log js/console name)
  (let [task-map (task-name->entry (keyword (apply str (drop 1 name))))] 
    (str "<p class='catalog'>"
         "<table><tbody>" 
         (apply str 
                (map (fn [[k v]]
                       (str 
                         "<tr>"
                         "<td class='catalog'>"
                         k
                         "</td>"
                         "<td class='catalog'>"
                         v
                         "</td>"
                         "</tr>"))
                     task-map))
         "</tbody></table>"
         "</p>")))

(defn build []
  (let [g (-> (Graph.)
              (.setGraph #js {})
              (.setDefaultEdgeLabel (fn [] #js {})))
        catalog (:catalog job)
        task-name->entry (zipmap (map :onyx/name catalog) catalog)]
    (doall 
      (for [t catalog]
        (let [task-name (str (:onyx/name t))] 
          (.setNode g 
                    task-name
                    #js {:description (str (:onyx/doc t))
                         :label task-name
                         :class (task-class t)}))))

    (.forEach (.nodes g)
              (fn [v]
                (let [node (.node g v)]
                  (set! (.-rx node) 5)
                  (set! (.-ry node) 5))))

    (doall
      (for [[t1 t2] (:workflow job)]
        ;; TODO: add flow condition connections here
        (.setEdge g (str t1) (str t2) #js {})))

    (let [R (.-render js/dagreD3)
          render (R.)
          svg (.select js/d3 "#svg-canvas")
          svg-group (.append svg "g")
          inner (.select js/d3 "#svg-canvas g")
          _ (render inner g)
          center-offset (/ (- (.attr svg "width") (.-width (.graph g))) 2)]
      (.attr svg-group "transform" (str "translate(" center-offset ", 20)"))
      (.attr svg "height" (+ (.-height (.graph g)) 40))
      (-> (.selectAll inner "g.node")
          (.attr "title" (partial style-tooltip task-name->entry))
          (.each (fn [v] 
                   (.tipsy (js/$ (js* "this")) #js {:gravity "w" 
                                                    :opacity 1 
                                                    :html true})))))))

(defn dagre [data owner]
  (reify
    om/IDidMount
    (did-mount [_]
      ;(js/attach)
      (build)
      (println "mounting"))
    om/IWillUnmount
    (will-unmount [_]
      (println "unmounting this"))
    om/IRender
    (render [_]
      (sab/html [:div
		 [:svg {:id "svg-canvas" :width 960 :height 600}]
		 [:h1 "This is your first devcard!"]]))))

(defcard-om first-card
  dagre
  {})

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

