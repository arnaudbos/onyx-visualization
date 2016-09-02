(ns onyx-viz.core
  (:require-macros [fence.core :refer [+++]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as sab :include-macros true]))

(enable-console-print!)

(def Graph (+++ (.-Graph (.-graphlib js/dagreD3))))
        
(defn task-class [task]
  (case (:onyx/type task)
    :input "type-input"
    :function "type-function"
    :output "type-output"))

;; TODO: switch this to sablono and render to html
(defn style-tooltip [task-name->entry name]
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

(defn add-tipsy [_]
  (let [this (js* "this")] 
    (+++ (.tipsy (js/$ this) #js {:gravity "w" 
                                  :opacity 1 
                                  :html true}))))

(defn build [job]
  (let [g (+++ (.setDefaultEdgeLabel (.setGraph (Graph.) #js {})
                                     (fn [] #js {})))
          catalog (:catalog job)
          task-name->entry (zipmap (map :onyx/name catalog) catalog)]
      (doall 
        (for [t catalog]
          (let [task-name (str (:onyx/name t))] 
            (+++ (.setNode g 
                           task-name
                           #js {:description (str (:onyx/doc t))
                                :label task-name
                                :class (task-class t)})))))

      (comment (.forEach (+++ (.nodes g))
                (fn [v]
                  (let [node (+++ (.node g v))
                        rx (+++ (.-rx node))
                        ry (+++ (.-ry node))]
                    (set! rx 5)
                    (set! ry 5)))))

      (doall
        (for [[t1 t2] (:workflow job)]
          ;; TODO: add flow condition connections here
          (+++ (.setEdge g (str t1) (str t2) #js {}))))

      (let [R         (+++ (.-render js/dagreD3))
            render    (R.)
            svg       (-> js/d3 (.select "#svg-canvas"))
            svg-group (-> svg (.append "g"))
            inner     (-> js/d3 (.select "#svg-canvas g"))
            _         (render inner g)
            ; center-offset (+++ (/ (- (.attr svg "width") (.-width (.graph g))) 2))
            ]
        ; commented centering graph because of flick when first zoom event applied
        ; due to first zoom event is based on 0,0 instead of center point
        ; (+++ (.attr svg-group "transform" (str "translate(" center-offset ", 20)")))
        
        (+++ (.attr svg "height" (+ (.-height (.graph g)) 40)))
        (+++ (.each (.attr (.selectAll inner "g.node")
                           "title" 
                           #(style-tooltip task-name->entry %))
                    add-tipsy))

        ; add zoom behaviour
        (-> svg 
            (.call (-> (js/d3.behavior.zoom)
                       (.on "zoom" (fn  []
                                        (.attr svg-group "transform"
                                                         (str "translate("
                                                              js/d3.event.translate
                                                              ") scale("
                                                              js/d3.event.scale
                                                              ")"))))))))))

(defn job-dag [{:keys [job width height]} owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (build job))
    om/IRender
    (render [_]
      (sab/html [:svg {:id "svg-canvas" :width width :height height}]))))
