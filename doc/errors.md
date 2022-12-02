# Errors

A collection of possible [HTTP errors codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) that you might be exposed to. We tried to stick to their original meaning as. If you have feedback regarding usage feel free to open an issue.

| Code | Message                       | Explanation                                                   | Fix                                                                           |
| 500  | Something went wrong          | Server can't handle request, this shouldn't happen            | Please open an issue                                                          |
| 401  | Authorzation token is missing | The request you sent didn't include an "Authorization" header | Add an "Authorization":"{token}" pair to your request (i.e in Fetch or Axios) |
| 403  | Malformed token               | Either something changed your token or it is outdated         | Generate a new token on the dashboard                                         |
| 404  | Couldn't find {X} with id {Y} | Y doesn't exist on X                                          | Check if you successfully created the item                                    |
