(ns onyx-viz.core
  (:require
   [om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-om deftest]]))

(enable-console-print!)

(defn dagre [data owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (js/attach)
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

