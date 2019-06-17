val m = Map(
  "name" -> "john doe",
  "age" -> 18,
  "hasChild" -> true,
  "childs" -> List(
    Map("name" -> "dorothy", "age" -> 5, "hasChild" -> false),
    Map("name" -> "bill", "age" -> 8, "hasChild" -> false)
  )
)
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
implicit val formats = Serialization.formats(NoTypeHints)
//List to json
val json1 = List(1, 2, 3)
compact(render(json1))
//Map to json
val json2 = ("name" -> "joe")
compact(render(json2))
//当有多行数据时，用~符号连接
val json3 = ("name" -> "joe") ~ ("age" -> 35)~("son"->"loulou")
val realJson=compact(render(json3))


val m2="name=loujianping;[age=30;[address=;[title=living=life;[son=[]"
val m3=m2.split(";\\[")
val m4=m3.map{x=>
  val a1=x.split("=",2)
  if(a1.apply(1)=="[]")(a1.apply(0),"")
  else (a1.apply(0),a1.apply(1))
}.toMap

import java.util.UUID

val uuid = UUID.randomUUID.toString
