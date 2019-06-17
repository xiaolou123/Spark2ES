package com.pingan.app

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.{SparkConf, SparkContext}

import com.alibaba.fastjson.JSON
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints

import org.elasticsearch.spark.rdd.EsSpark
import org.elasticsearch.spark.streaming.api.java.JavaEsSparkStreaming

/**
 *  foreachRDD
 */
object KafkaSparkScala {

  def main(args: Array[String]): Unit = {
    //创建streamingContext
    //var conf=new SparkConf().setMaster("yarn-client")
    var conf=new SparkConf().setMaster("local[3]").setAppName("kafkaspark").set("es.nodes","192.168.124.150").set("es.port","9200")
    val sc=new SparkContext(conf)
    var ssc=new StreamingContext(sc,Seconds(5))
    //创建topic
    //var topic=Map{"test" -> 1}
    var topics=Iterable("waf-log-test")
    //指定zookeeper
    //消费者配置
    val kafkaParam= Map [String,Object](
      "bootstrap.servers" -> "192.168.124.134:9092,192.168.124.135:9092,192.168.124.136:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "kafkaConsumerGroup-01",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "auto.create" -> (true: java.lang.Boolean)
    )


    //创建DStream，返回接收到的输入数据
    var data:InputDStream[ConsumerRecord[String,String]]=
      KafkaUtils.createDirectStream[String,String](ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String,String](topics,kafkaParam))

    //每一个stream都是一个ConsumerRecord
    val message=data.foreachRDD { rdd =>
      //println("进入InputDStream[ConsumerRecord[String,String]]::"+rdd)
      val json = rdd.map { consumer =>
        //println("进入ConsumerRecord[String,String]:::"+consumer)
        val v1: String = consumer.value()
        //println("v1::::"+v1)
        val obj: com.alibaba.fastjson.JSONObject = JSON.parseObject(v1)
        val logStr: String = obj.getString("message")
        println("logStr::::::::::::::::"+logStr)
        val log: Array[String] = logStr.split(";\\[")
        val result = log.map { x =>
          val a1 = x.split("=", 2)
          (a1.apply(0), a1.apply(1))
        }.toMap
        implicit val formats = Serialization.formats(NoTypeHints)
        val resultJson = compact(render(result))
        resultJson
      }
      println("json:::::::"+json)
      EsSpark.saveJsonToEs(json,"waf-log-test/wlt")
    }


    ssc.start()
    ssc.awaitTermination()
  }
}
