package com.dexter.baseproject.scopes

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope


@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class ActivityScope