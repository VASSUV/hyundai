package ru.example.hyundai.gen


//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: cars.proto

@kotlin.jvm.JvmSynthetic
inline fun price(block: PriceKt.Dsl.() -> Unit): CarsOuterClass.Price =
  PriceKt.Dsl._create(CarsOuterClass.Price.newBuilder()).apply { block() }._build()
object PriceKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  class Dsl private constructor(
    @kotlin.jvm.JvmField private val _builder: CarsOuterClass.Price.Builder
  ) {
    companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: CarsOuterClass.Price.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): CarsOuterClass.Price = _builder.build()

    /**
     * <code>string id = 1;</code>
     */
    var id: kotlin.String
      @JvmName("getId")
      get() = _builder.getId()
      @JvmName("setId")
      set(value) {
        _builder.setId(value)
      }
    /**
     * <code>string id = 1;</code>
     */
    fun clearId() {
      _builder.clearId()
    }

    /**
     * <code>string maxValue = 2;</code>
     */
    var maxValue: kotlin.String
      @JvmName("getMaxValue")
      get() = _builder.getMaxValue()
      @JvmName("setMaxValue")
      set(value) {
        _builder.setMaxValue(value)
      }
    /**
     * <code>string maxValue = 2;</code>
     */
    fun clearMaxValue() {
      _builder.clearMaxValue()
    }

    /**
     * <code>string minValue = 3;</code>
     */
    var minValue: kotlin.String
      @JvmName("getMinValue")
      get() = _builder.getMinValue()
      @JvmName("setMinValue")
      set(value) {
        _builder.setMinValue(value)
      }
    /**
     * <code>string minValue = 3;</code>
     */
    fun clearMinValue() {
      _builder.clearMinValue()
    }
  }
}
@kotlin.jvm.JvmSynthetic
inline fun CarsOuterClass.Price.copy(block: PriceKt.Dsl.() -> Unit): CarsOuterClass.Price =
  PriceKt.Dsl._create(this.toBuilder()).apply { block() }._build()
