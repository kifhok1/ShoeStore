import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import com.example.shoestore.R
import com.example.shoestore.data.model.OnboardingPage
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.WhiteButton

@Composable
fun OnBoardingScreen(
    onFinished: () -> Unit
) {
    Log.d("OnBoarding", "OnBoardingScreen запущен")

    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.image_1,
            title = stringResource(R.string.Welcome),
            description = ""
        ),
        OnboardingPage(
            imageRes = R.drawable.image_2,
            title = stringResource(R.string.Lets_Start),
            description = stringResource(R.string.Collection_Explore_Now)
        ),
        OnboardingPage(
            imageRes = R.drawable.image_3,
            title = stringResource(R.string.Power_To),
            description = stringResource(R.string.Plants_To_Your_Room)
        )
    )

    // !!! КРИТИЧНО: pages.size + 1 для создания скрытой страницы
    val pagerState = rememberPagerState(pageCount = { pages.size + 1 })
    val scope = rememberCoroutineScope()

    // Отслеживаем свайп на скрытую страницу
    LaunchedEffect(pagerState.currentPage) {
        Log.d("OnBoarding", "Текущая страница: ${pagerState.currentPage}, Всего страниц: ${pages.size}")
        if (pagerState.currentPage == pages.size) {
            Log.d("OnBoarding", "Свайп на скрытую страницу! Вызываем onFinished()")
            onFinished()
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF48B2E8),
            Color(0xFF0076B2)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            if (pageIndex < pages.size) {
                val page = pages[pageIndex]

                val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                val imageAlpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = imageAlpha
                            scaleX = 0.9f + (0.1f * imageAlpha)
                            scaleY = 0.9f + (0.1f * imageAlpha)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (pageIndex == 0) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(70.dp))
                            Text(
                                text = page.title,
                                style = CustomTheme.typography.HeadingBold30,
                                color = CustomTheme.colors.block,
                                textAlign = TextAlign.Center,
                                lineHeight = 40.sp,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = page.imageRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(0.8f))
                        Image(
                            painter = painterResource(id = page.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = page.title,
                            style = CustomTheme.typography.HeadingRegular34,
                            color = CustomTheme.colors.block,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (page.description.isNotEmpty()) {
                            Text(
                                text = page.description,
                                style = CustomTheme.typography.BodyRegular16,
                                color = CustomTheme.colors.subTextLight,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(120.dp))
                }
            } else {
                // Скрытая страница (пустая)
                Box(Modifier.fillMaxSize())
            }
        }

        // Прячем элементы управления на скрытой странице
        if (pagerState.currentPage < pages.size) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        val width = if (isSelected) 43.dp else 28.dp
                        val color = if (isSelected) Color.White else Color(0xFFD4D4D4).copy(alpha = 0.5f)

                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(width)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                    }
                }

                WhiteButton(
                    onClick = {
                        Log.d("OnBoarding", "Кнопка нажата на странице ${pagerState.currentPage}")
                        scope.launch {
                            // !!! КРИТИЧНО: pages.size - 1 (то есть < 2 для 3 страниц)
                            if (pagerState.currentPage < pages.size - 1) {
                                val nextPage = pagerState.currentPage + 1
                                Log.d("OnBoarding", "Переход на страницу $nextPage")
                                pagerState.animateScrollToPage(nextPage)
                            } else {
                                // Мы на последней реальной странице (индекс 2)
                                Log.d("OnBoarding", "Последняя страница! Вызываем onFinished()")
                                onFinished()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    text = when (pagerState.currentPage) {
                        0 -> stringResource(R.string.Start)
                        pages.size - 1 -> stringResource(R.string.Start) // или "Готово"
                        else -> stringResource(R.string.Next)
                    }
                )
            }
        }
    }
}
