### XXX

I have a problem which I guess is about macros and how clojurescript deals with them. But I have no idea what is going on exactly. Can someone shed a light?

I created file app/playground.cljs which is not referenced by any other file in the project. If namespace app.playground requires somethin from Fulcro I get errors when using def form.

Everything works fine if namespace app.playground is required in entry
namespace app.client:

```clojure
ns app.client
  (:require
   [app.playground]
   ...)
```

shadow-cljs.edn :

```
{:builds
 {:main {:modules {:main {:init-fn app.client/init
                          :entries [app.client]}}}}}
```

