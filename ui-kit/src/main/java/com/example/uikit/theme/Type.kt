package ru.svolf.trich.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import ru.svolf.trich.uikit.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val OpenSansFont = GoogleFont("Open Sans")

val OpenSansFontFamily = FontFamily(
    Font(googleFont = OpenSansFont, fontProvider = provider, weight = FontWeight(350)),
    Font(googleFont = OpenSansFont, fontProvider = provider, weight = FontWeight(500)),
    Font(googleFont = OpenSansFont, fontProvider = provider, weight = FontWeight(700))
)

val defaultTypography = Typography()

// Set of Material typography styles to start with
val Typography =
    Typography(
        displayLarge = defaultTypography.displayLarge.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(700)
        ),
        displayMedium = defaultTypography.displayMedium.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(700)
        ),
        displaySmall = defaultTypography.displaySmall.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(700)
        ),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(700)
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(700)
        ),
        headlineSmall = defaultTypography.headlineSmall.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(500)
        ),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(700)
        ),
        titleMedium = defaultTypography.titleMedium.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(500)
        ),
        titleSmall = defaultTypography.titleSmall.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(500)
        ),
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(350)
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(350)
        ),
        bodySmall = defaultTypography.bodySmall.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(350)
        ),
        labelLarge = defaultTypography.labelLarge.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(500)
        ),
        labelMedium = defaultTypography.labelMedium.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(500)
        ),
        labelSmall = defaultTypography.labelSmall.copy(
            fontFamily = OpenSansFontFamily,
            fontWeight = FontWeight(500)
        )
    )
