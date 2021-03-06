package ru.example.hyundai.gen

import ru.example.hyundai.gen.Allcars

//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: allcars.proto

@kotlin.jvm.JvmSynthetic
inline fun carPreview(block: CarPreviewKt.Dsl.() -> Unit): Allcars.CarPreview =
  CarPreviewKt.Dsl._create(Allcars.CarPreview.newBuilder()).apply { block() }._build()
object CarPreviewKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  class Dsl private constructor(
    @kotlin.jvm.JvmField private val _builder: Allcars.CarPreview.Builder
  ) {
    companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: Allcars.CarPreview.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): Allcars.CarPreview = _builder.build()

    /**
     * <code>string name = 1;</code>
     */
    var name: kotlin.String
      @JvmName("getName")
      get() = _builder.getName()
      @JvmName("setName")
      set(value) {
        _builder.setName(value)
      }
    /**
     * <code>string name = 1;</code>
     */
    fun clearName() {
      _builder.clearName()
    }

    /**
     * <code>uint32 id = 2;</code>
     */
    var id: kotlin.Int
      @JvmName("getId")
      get() = _builder.getId()
      @JvmName("setId")
      set(value) {
        _builder.setId(value)
      }
    /**
     * <code>uint32 id = 2;</code>
     */
    fun clearId() {
      _builder.clearId()
    }

    /**
     * <code>string link = 3;</code>
     */
    var link: kotlin.String
      @JvmName("getLink")
      get() = _builder.getLink()
      @JvmName("setLink")
      set(value) {
        _builder.setLink(value)
      }
    /**
     * <code>string link = 3;</code>
     */
    fun clearLink() {
      _builder.clearLink()
    }

    /**
     * <code>string img = 4;</code>
     */
    var img: kotlin.String
      @JvmName("getImg")
      get() = _builder.getImg()
      @JvmName("setImg")
      set(value) {
        _builder.setImg(value)
      }
    /**
     * <code>string img = 4;</code>
     */
    fun clearImg() {
      _builder.clearImg()
    }

    /**
     * <code>string modelinfolink = 5;</code>
     */
    var modelinfolink: kotlin.String
      @JvmName("getModelinfolink")
      get() = _builder.getModelinfolink()
      @JvmName("setModelinfolink")
      set(value) {
        _builder.setModelinfolink(value)
      }
    /**
     * <code>string modelinfolink = 5;</code>
     */
    fun clearModelinfolink() {
      _builder.clearModelinfolink()
    }

    /**
     * <code>uint32 show = 6;</code>
     */
    var show: kotlin.Int
      @JvmName("getShow")
      get() = _builder.getShow()
      @JvmName("setShow")
      set(value) {
        _builder.setShow(value)
      }
    /**
     * <code>uint32 show = 6;</code>
     */
    fun clearShow() {
      _builder.clearShow()
    }

    /**
     * <code>uint32 isSubscribeActive = 7;</code>
     */
    var isSubscribeActive: kotlin.Int
      @JvmName("getIsSubscribeActive")
      get() = _builder.getIsSubscribeActive()
      @JvmName("setIsSubscribeActive")
      set(value) {
        _builder.setIsSubscribeActive(value)
      }
    /**
     * <code>uint32 isSubscribeActive = 7;</code>
     */
    fun clearIsSubscribeActive() {
      _builder.clearIsSubscribeActive()
    }
  }
}
@kotlin.jvm.JvmSynthetic
inline fun Allcars.CarPreview.copy(block: CarPreviewKt.Dsl.() -> Unit): Allcars.CarPreview =
  CarPreviewKt.Dsl._create(this.toBuilder()).apply { block() }._build()
