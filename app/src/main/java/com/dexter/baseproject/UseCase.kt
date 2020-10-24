package com.dexter.baseproject

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface UseCase<RequestT, ResponseT> {
    fun execute(req: RequestT): Observable<Result<ResponseT>>

    companion object {

        fun <ResponseT> wrapObservable(s: Observable<ResponseT>): Observable<Result<ResponseT>> {
            return s.map<Result<ResponseT>> { Result.Success(it) }
                .startWith(Result.Progress())
                .onErrorReturn {
                    Result.Failure(it)
                }
        }

        fun <ResponseT> wrapSingle(s: Single<ResponseT>): Observable<Result<ResponseT>> {
            return wrapObservable<ResponseT>(s.flatMapObservable<ResponseT> { Observable.just(it) })
        }

        fun wrapCompletable(c: Completable): Observable<Result<Unit>> {
            return wrapSingle(
                c.andThen(Single.just(Unit))
            )
        }


    }
}