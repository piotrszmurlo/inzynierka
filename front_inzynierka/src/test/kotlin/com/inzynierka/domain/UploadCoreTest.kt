package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.UploadAction
import com.inzynierka.domain.core.UploadFilesState
import com.inzynierka.domain.core.uploadReducer
import io.kvision.redux.createTypedReduxStore
import io.kvision.types.KFile
import kotlin.test.*

class UploadCoreTest {
    private val initialUploadState = UploadFilesState()
    private val store = createTypedReduxStore(::uploadReducer, initialUploadState)

    @Test
    fun test_upload_file_started() {
        store.dispatch(UploadAction.UploadFileStarted)
        assertTrue(store.getState().isUploading)
    }

    @Test
    fun test_upload_file_success() {
        store.dispatch(UploadAction.UploadFileSuccess)
        assertFalse(store.getState().isUploading)
        assertNull(store.getState().kFiles)
        assertTrue(store.getState().success!!)
    }

    @Test
    fun test_upload_file_failed() {
        val error = DomainError("Upload failed")
        store.dispatch(UploadAction.UploadFileFailed(error))
        assertFalse(store.getState().isUploading)
        assertEquals(error, store.getState().error)
    }

    @Test
    fun test_upload_form_on_change_handler() {
        val files = listOf(KFile("file1.txt", 10), KFile("file2.txt", 20))
        store.dispatch(UploadAction.UploadFormOnChangeHandler(files))
        assertEquals(files, store.getState().kFiles)
    }

    @Test
    fun test_result_handled() {
        store.dispatch(UploadAction.ResultHandled)
        assertNull(store.getState().error)
        assertNull(store.getState().success)
        assertNull(store.getState().kFiles)
    }
}