package com.github.plokhotnyuk.jsoniter_scala.examples

import java.nio.charset.StandardCharsets.UTF_8

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.util.hashing.MurmurHash3

/**
  * Example of basic usage from README.md
  */
object Example01 {
  object RawVal {
    def apply(s: String) = new RawVal(s.getBytes)

    implicit val codec: JsonValueCodec[RawVal] = new JsonValueCodec[RawVal] {
      override def decodeValue(in: JsonReader, default: RawVal): RawVal = new RawVal(in.readRawValAsBytes())

      override def encodeValue(x: RawVal, out: JsonWriter): Unit = out.writeRawVal(x.bs)

      override val nullValue: RawVal = new RawVal(new Array[Byte](0))
    }
  }

  case class RawVal private(bs: Array[Byte]) {
    def this(s: String) = this(s.getBytes(UTF_8))

    override lazy val hashCode: Int = MurmurHash3.arrayHash(bs)

    override def equals(obj: Any): Boolean = obj match {
      case that: RawVal => java.util.Arrays.equals(bs, that.bs)
      case _ => false
    }
  }

  case class JobJson(name:String,id:String,dataflowId:String,properties:Properties)
  case class Properties(activities:List[Activity],activityGroup:List[ActivityGroup])
  case class Activity(name:String,types:String,id:String,inputs:List[Inputs],outputs:List[Outputs],runtimeInfo:Map[String,RawVal],start:String,end:String)
  case class Inputs(properties:InputProperties,sourceConnection:SourceConnection)
  case class InputProperties(structure:List[Structure],translator:List[Translator])
  case class Structure(index:Int,name:String,dataType:String)
  case class Translator(index:Int,name:String,dataType:String)
  case class SourceConnection(connectionType:String,fileName:String,linkedService:String,location:String,datasetId:String,datafileId:String)
  case class Outputs(properties:OutputProperties,destConnection:DestConnection)
  case class OutputProperties(structure:List[OutputStructure],translator:String)
  case class OutputStructure(index:Int,name:String,dataType:String)
  case class DestConnection(connectionType:String,fileName:String,linkedService:String,location:String,datasetId:String,datafileId:String)
  case class ActivityGroup(name:String,id:String,properties:ActivityGroupProperty)
  case class ActivityGroupProperty(activityList:List[ActivityList])
  case class ActivityList(id:String,order:String)

  implicit val codec: JsonValueCodec[JobJson] = JsonCodecMaker.make(CodecMakerConfig)

  def main(args: Array[String]): Unit = {
    val job = readFromArray(
      """
        {
      "name": "new flow test 3",
      "id": "2e456f79-b62d-44df-a809-18a2e0af9377",
      "dataflowId": "2e456f79-b62d-44df-a809-18a2e0af9377",

      "properties": {
        "Activities": [
          {
            "name": "Time Series Anamoly Detection",
            "types": "Robust Principal Component Analysis",
            "id": "g0a1",
            "inputs": [
              {
                "properties": {
                  "structure": [
                    {
                      "index": 1,
                      "name": "Survived",
                      "dataType": "integer"
                    },
                    {
                      "index": 2,
                      "name": "Pclass",
                      "dataType": "integer"
                    }
                  ],
                  "translator": [
                    {
                      "index": 1,
                      "name": "Survived",
                      "dataType": "integer"
                    },
                    {
                      "index": 2,
                      "name": "Pclass",
                      "dataType": "integer"
                    }
                  ]
                },
                "sourceConnection": {
                  "connectionType": "AzureBlob",
                  "fileName": "Titanic.csv",
                  "linkedService": "sasurl",
                  "location": "wasbs://DFC33643-64A3-4A15-B3C0-17AA97C37FFD@olympusstoragedev.blob.core.windows.net/F2E42EC7-635E-4522-8EB2-2270D7AB4387/F011ED33-FBF3-46FB-BB0B-A797734257FC/pqlzn.csv",
                  "datasetId": "F2E42EC7-635E-4522-8EB2-2270D7AB4387",
                  "datafileId": "F011ED33-FBF3-46FB-BB0B-A797734257FC"
                }
              }
            ],
            "outputs": [
              {
                "properties": {
                  "structure": [
                    {
                      "index": 1,
                      "name": "Survived",
                      "dataType": "integer"
                    },
                    {
                      "index": 2,
                      "name": "Pclass",
                      "dataType": "integer"
                    }
                  ],
                  "translator": null
                },
                "destConnection": {
                  "connectionType": "AzureBlob",
                  "fileName": "Titanic.csv",
                  "linkedService": "sasurl",
                  "location": "wasbs://DFC33643-64A3-4A15-B3C0-17AA97C37FFD@olympusstoragedev.blob.core.windows.net/F2E42EC7-635E-4522-8EB2-2270D7AB4387/F011ED33-FBF3-46FB-BB0B-A797734257FC/pqlzn.csv",
                  "datasetId": "F2E42EC7-635E-4522-8EB2-2270D7AB4387",
                  "datafileId": "F011ED33-FBF3-46FB-BB0B-A797734257FC"
                }
              }
            ],
            "runtimeInfo": {
              "metric": "Survived",
              "defineOutput": "Exclude Anomaly",
              "selectDateColumn": "Pclass"
            },
            "start": "st",
            "end": "end"
          },
          {
            "name": "Seetal Dataset",
            "types": "output",
            "id": "g0a2",
            "inputs": [
              {
                "properties": {
                  "structure": [
                    {
                      "index": 1,
                      "name": "Survived",
                      "dataType": "integer"
                    },
                    {
                      "index": 2,
                      "name": "Pclass",
                      "dataType": "integer"
                    }
                  ],
                  "translator": [
                    {
                      "index": 1,
                      "name": "Survived",
                      "dataType": "integer"
                    },
                    {
                      "index": 2,
                      "name": "Pclass",
                      "dataType": "integer"
                    }
                  ]
                },
                "sourceConnection": {
                  "connectionType": "AzureBlob",
                  "fileName": "Titanic.csv",
                  "linkedService": "sasurl",
                  "location": "wasbs://DFC33643-64A3-4A15-B3C0-17AA97C37FFD@olympusstoragedev.blob.core.windows.net/F2E42EC7-635E-4522-8EB2-2270D7AB4387/F011ED33-FBF3-46FB-BB0B-A797734257FC/pqlzn.csv",
                  "datasetId": "F2E42EC7-635E-4522-8EB2-2270D7AB4387",
                  "datafileId": "F011ED33-FBF3-46FB-BB0B-A797734257FC"
                }
              }
            ],
            "outputs": [
              {
                "properties": {
                  "structure": [
                    {
                      "index": 1,
                      "name": "Survived",
                      "dataType": "integer"
                    },
                    {
                      "index": 2,
                      "name": "Pclass",
                      "dataType": "integer"
                    }
                  ],
                  "translator": null
                },
                "destConnection": {
                  "connectionType": "AzureBlob",
                  "fileName": "Titanic.csv",
                  "linkedService": "sasurl",
                  "location": "wasbs://DFC33643-64A3-4A15-B3C0-17AA97C37FFD@olympusstoragedev.blob.core.windows.net/F2E42EC7-635E-4522-8EB2-2270D7AB4387/F011ED33-FBF3-46FB-BB0B-A797734257FC/F011ED33-FBF3-46FB-BB0B-A797734257FC.csv",
                  "datasetId": "F2E42EC7-635E-4522-8EB2-2270D7AB4387",
                  "datafileId": "F011ED33-FBF3-46FB-BB0B-A797734257FC"
                }
              }
            ],
            "runtimeInfo": {

            },
            "start": "st",
            "end": "end"
          }
        ],
    "activityGroup": [
                {
                    "name": "grp1",
                    "id": "g0",
                    "properties": {
                        "activityList": [
                            {
                                "id": "g0a0",
                                "order": "0"
                            },
                            {
                                "id": "g0a1",
                                "order": "1"
                            },
                            {
                                "id": "g0a2",
                                "order": "2"
                            }
                        ]
                    }
                }
            ]

      }

    }
        """.getBytes("UTF-8"))
    val json = writeToArray(job)

    println(job)
    println(new String(json, "UTF-8"))
  }
}
