# crud - Build your backend with your frontend

This repository contains all the backend services for crud. It's written entirely in Clojure.

## Requirements

- JDK 8 (or above)
- Clojure 1.10

crud uses a MongoDB as it's main database, so it's best to also have a MongoDB v6.0 (earlier should work, too) installed and running.

## Installation

Clone the repo

```
git clone git@github.com:gedankenessen/crud-backend.git
```

The necessary deps will be downloaded once you jack-in with your REPL. But you can also download the deps by running

```
lein deps
```

As of right now crud expects a MongoDB running on the default port. You can change the behavior in `crud.persistence.mongo`.

## Usage

1. Open your favorite editor and navigate to the `crud.core` namespace.
2. Once there, start your REPL
3. Either call the `-main` function or evaluate the `start-server` call

The API is now available under `localhost:3004/`. You can interact with crud by making HTTP requests.

## Tests

You can run specific tests in your REPL or use lein to run them all with

```
lein test
```

## Examples

TODO: FIXME

## Roadmap

TODO: FIXME

## Edgecases

TODO: FIXME

### Bugs

TODO: FIXME


<hr/>
<div align="center" >
<a href="https://gedankenessen.github.io/crud-landing/">
<img width="640" alt="card" src="https://user-images.githubusercontent.com/24259317/202017966-83501535-4b8a-40cd-ae79-ac5e5f8d1d41.png">
</a>
</div>

