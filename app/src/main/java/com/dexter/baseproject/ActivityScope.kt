package com.dexter.baseproject

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope


@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class ActivityScope