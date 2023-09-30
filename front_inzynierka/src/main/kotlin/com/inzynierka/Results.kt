package com.inzynierka

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.Result
import com.inzynierka.domain.service.IDataService
import io.kvision.chart.ChartType
import io.kvision.chart.Configuration
import io.kvision.chart.DataSets
import io.kvision.chart.chart
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.panel.vPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Container.results(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {

    button("Fetch data").onClick {
        store.dispatch { dispatch, _ ->
            dispatch(MainAppAction.FetchDataStarted)
            CoroutineScope(Dispatchers.Default).launch {
                when (val result = dataService.getData()) {
                    is Result.Success -> {
                        dispatch(MainAppAction.FetchDataSuccess(result.data.data))
                    }

                    is Result.Error -> {
                        dispatch(MainAppAction.FetchDataFailed(result.domainError))
                    }
                }
            }
        }
    }

    vPanel().bind(store.sub(extractor = { state -> state.data })) { state ->
        alignItems = AlignItems.CENTER
        chart(
            Configuration(
                ChartType.BAR,
                listOf(DataSets(data = state)),
                listOf("One", "Two", "Three", "Four")
            ),
            600, 400
        )
    }

}