import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.BirdImage
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class BirdsUiState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null
) {
    val categories = images.map { it.category }.toSet()
    val selectedImage = images.filter { it.category == selectedCategory }
}

class BirdsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BirdsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateImages()
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }


    fun selectedCategory(category: String?) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    private fun updateImages() {
        viewModelScope.launch {
            val images = getImages()
            _uiState.update {
                it.copy(images = images)
            }
        }

    }

    private suspend fun getImages(): List<BirdImage> =
        httpClient.get("https://sebi.io/demo-image-api/pictures.json").body()

    override fun onCleared() {
        httpClient.close()
    }
}