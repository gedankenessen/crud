# crud - Prototype your frontend with your backend

Prototype faster by having crud create your basic CRUD backend. All you need to do is send over JSON - and crud will handle the rest.

<div align="center" >
  <a href="https://crud.gedankenessen.de">
    <img width="320" alt="Prototype your backend with your frontend" src="https://repository-images.githubusercontent.com/561455835/b072503c-2e12-4383-9707-9fc1f20bd488">
  </a>
</div>

# Usage

`POST` your JSON to the `endpoint` of your choice:

```
POST 127.0.0.1/build/users
{
  "name": "tom",
  "theme": "dark",
  "birthday": "1996-01-01T00:00:00.000Z"
}
```

crud will automatically generate all CRUD operations on that `endpoint`:

```
/users/{id}
GET
GET {id}
POST {id}
PUT {id}
DELETE {id}
```

If the structure of your data changes, crud automatically wipes the database:

```
POST 127.0.0.1/build/users
{
  "name": "jenna",
  "theme": "light",
  "birthday": "1996-01-01T00:00:00.000Z",
  "lastLogin": "2022-03-15T20:01:22.614Z",
}
```

```
GET 127.0.0.1/build/users
[{
  "name": "jenna",
  "theme": "light",
  "birthday": "1996-01-01T00:00:00.000Z",
  "lastLogin": "2022-03-15T20:01:22.614Z",
  "id": "42822af8-0d95-42de-aa7f”,
},]
```

# Installation

Checkout the wiki: [crud.gedankenessen.de/wiki](https://crud.gedankenessen.de/wiki/installation).

# More information

Checkout [crud.gedankenessen.de](https://crud.gedankenessen.de) for more information.

# Development

## Requirements

- JDK 8 (or above)
- Clojure 1.10

If you want your data [to be persisted](https://crud.gedankenessen.de/wiki/installation#about):
- MongoDB v6.0

## Installation

Clone the repo

```
git clone git@github.com:gedankenessen/crud-backend.git
```

The necessary deps will be downloaded once you jack-in with your REPL. But you can also download the deps by running

```
lein deps
```

## Usage

1. Open your favorite code editor and navigate to the `crud.core` namespace.
2. Once there, start your REPL
3. Either call the `-main` function or evaluate the `start-server` call

The API is now available under `localhost:3004/`. You can interact with crud by making HTTP requests.

## Tests

You can run specific tests in your REPL or use lein to run them all with

```
lein test
```
