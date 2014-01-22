package manu.tron.web

import com.twitter.finatra._

object App extends FinatraServer {

  register(IndexController)

}
