## onyx-visualization

Provides visualizations of Onyx jobs. Currently used in onyx-dashboard.

## Development
`lein figwheel`  
and then go to  
`http://localhost:3449/cards.html`  
Before do PR, ensure to test code in production mode by:  
```bash
# in another tab
rm -f resources/public/js/compiled/onyx_viz.js && \
lein cljsbuild once prod
```
and go to   
`http://localhost:3449/index.html`
