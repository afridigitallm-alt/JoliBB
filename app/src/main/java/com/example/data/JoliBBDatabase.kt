package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Product::class, CartItem::class], version = 1, exportSchema = false)
abstract class JoliBBDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var INSTANCE: JoliBBDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): JoliBBDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JoliBBDatabase::class.java,
                    "jolibb_database"
                )
                .addCallback(JoliBBDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class JoliBBDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.productDao())
                }
            }
        }

        private suspend fun populateDatabase(productDao: ProductDao) {
            val initialProducts = listOf(
                Product(
                    id = 1,
                    title = "Kit Naissance Velours \"Mon Premier Jour\"",
                    sku = "KIT-NS-01",
                    priceXaf = 15000.0,
                    priceEur = 22.90,
                    descriptionShort = "Pack de 5 pièces indispensables pour la maternité (bonnet, moufles, pyjama, body, bavoir).",
                    descriptionLong = "Préparez la venue au monde de votre trésor avec ce kit naissance complet de 5 pièces de layette délicate. Conçu pour le premier jour à la maternité, il comprend un pyjama croisé en velours doux, un body manches longues assorti, un bavoir brodé, des moufles anti-griffures et un petit bonnet chaud. Un indispensable absolu pour le trousseau de naissance de votre bébé.",
                    imagePath = "img_logo",
                    categories = "Layette Naissance",
                    tags = "Pack naissance, Nouveauté",
                    stock = 15,
                    sizes = "Naissance, 1 mois",
                    colors = "Blanc cassé, Rose poudré, Bleu pastel",
                    material = "80% Coton biologique peigné, 20% Polyester doux",
                    entretien = "Lavage en machine à 30°C cycle délicat",
                    rating = 4.9f,
                    reviewCount = 18,
                    isPromo = false
                ),
                Product(
                    id = 2,
                    title = "Body Croisé en Coton Bio (Lot de 3)",
                    sku = "BDY-CB-02",
                    priceXaf = 8500.0,
                    priceEur = 12.95,
                    descriptionShort = "Lot de 3 bodies croisés manches longues avec boutons pression latéraux.",
                    descriptionLong = "Ce lot de 3 bodies croisés en coton 100% biologique protège la peau sensible de votre nouveau-né. Grâce à sa fermeture croisée par boutons pression sur le devant, habiller bébé devient un jeu d'enfant sans avoir à lui passer le vêtement par la tête. Indispensable pour les premiers mois de bébé à Douala, le tissu en coton peigné laisse respirer la peau tout en préservant la chaleur.",
                    imagePath = "img_logo",
                    categories = "Vêtements 0-3 mois, Layette Naissance",
                    tags = "Coton bio, Best-seller",
                    stock = 30,
                    sizes = "Naissance, 1 mois, 3 mois",
                    colors = "Blanc pur, Vert d'eau, Crème",
                    material = "100% Coton Biologique certifié GOTS",
                    entretien = "Lavage machine à 40°C, repassage doux",
                    rating = 4.8f,
                    reviewCount = 24,
                    isPromo = false
                ),
                Product(
                    id = 3,
                    title = "Pyjama Dors-Bien en Coton Bio",
                    sku = "PYJ-DB-03",
                    priceXaf = 9800.0,
                    priceEur = 14.95,
                    descriptionShort = "Pyjama dors-bien avec fermeture pressionnée asymétrique devant.",
                    descriptionLong = "Prenez soin du sommeil de votre bébé avec ce pyjama dors-bien en velours ultra-doux. Doté d'une fermeture asymétrique pressionnée sur le devant et à l'entrejambe pour un change express la nuit sans déshabiller complètement l'enfant. Ses pieds intégrés gardent bébé au chaud lors des nuits climatisées à Douala.",
                    imagePath = "img_logo",
                    categories = "Layette Naissance, Vêtements 0-3 mois",
                    tags = "Coton bio, Promo",
                    stock = 25,
                    sizes = "Naissance, 1 mois, 3 mois",
                    colors = "Rose poudré, Bleu ciel, Jaune miel",
                    material = "75% Coton peigné, 25% Polyester pour le velours",
                    entretien = "Lavage à 30°C, sèche-linge modéré",
                    rating = 4.7f,
                    reviewCount = 12,
                    isPromo = true,
                    promoDiscountPercent = 20
                ),
                Product(
                    id = 4,
                    title = "Brassière en Maille Tricot Douce",
                    sku = "BRS-TR-04",
                    priceXaf = 12000.0,
                    priceEur = 18.30,
                    descriptionShort = "Gilet brassière en tricot acrylique et coton très souple.",
                    descriptionLong = "La brassière tricotée est obligatoire dans le trousseau de maternité demandé dans les cliniques de Douala. Elle apporte une douce chaleur réconfortante dès la naissance de bébé. Boutonnage croisé avec lien intérieur et jolis boutons en bois sur le côté pour un style intemporel et chic inspiré de la layette classique française.",
                    imagePath = "img_logo",
                    categories = "Layette Naissance",
                    tags = "Tricot, Maternité",
                    stock = 12,
                    sizes = "Naissance, 1 mois",
                    colors = "Gris chiné, Beige sable, Écru",
                    material = "50% Coton de qualité supérieure, 50% Acrylique extra-doux",
                    entretien = "Lavage machine cycle laine ou lavage à la main recommandé",
                    rating = 4.6f,
                    reviewCount = 9,
                    isPromo = false
                ),
                Product(
                    id = 5,
                    title = "Gigoteuse d'été légère \"Doux Sommeil\"",
                    sku = "GIG-EL-05",
                    priceXaf = 18000.0,
                    priceEur = 27.45,
                    descriptionShort = "En mousseline de coton légère (TOG 1.0) parfaite pour le climat de Douala.",
                    descriptionLong = "Idéale pour le climat tropical de Douala, cette gigoteuse légère sans manches est réalisée en double gaze de coton biologique ultra-respirante. Elle maintient bébé à une température agréable pendant les siestes et les nuits sans risque de surchauffe. Grande fermeture éclair sécurisée latérale et boutons pression d'épaules pour faciliter l'installation de bébé.",
                    imagePath = "img_logo",
                    categories = "Accessoires",
                    tags = "Sommeil, Coton bio",
                    stock = 10,
                    sizes = "0-6 mois, 6-12 mois",
                    colors = "Beige abricot, Menthe douce, Blanc motif étoiles",
                    material = "100% Mousseline de double gaze de coton biologique",
                    entretien = "Lavage machine à 30°C, repassage inutile (aspect gaufré naturel)",
                    rating = 4.9f,
                    reviewCount = 15,
                    isPromo = false
                ),
                Product(
                    id = 6,
                    title = "Bonnet & Moufles de Naissance",
                    sku = "ACC-BM-06",
                    priceXaf = 4500.0,
                    priceEur = 6.85,
                    descriptionShort = "Ensemble bonnet rond et petites moufles anti-griffures assorties.",
                    descriptionLong = "Un joli petit lot indispensable pour les premiers jours. Le bonnet préserve la température corporelle de votre nouveau-né par la tête, tandis que les moufles douces l'empêchent de se griffer le visage involontairement. Réalisé dans une maille tricotée 100% coton bio extensible qui épouse parfaitement les contours de bébé.",
                    imagePath = "img_logo",
                    categories = "Accessoires, Layette Naissance",
                    tags = "Maternité, Protection",
                    stock = 40,
                    sizes = "Naissance",
                    colors = "Blanc pur, Rose layette, Bleu layette",
                    material = "100% Coton biologique peigné extensible",
                    entretien = "Lavage machine à 30°C",
                    rating = 4.7f,
                    reviewCount = 31,
                    isPromo = false
                ),
                Product(
                    id = 7,
                    title = "Nid d'ange rembourré \"Cocon Douillet\"",
                    sku = "NDA-CD-07",
                    priceXaf = 25000.0,
                    priceEur = 38.10,
                    descriptionShort = "Nid d'ange zippé de sortie à capuche doublée micro-polaire.",
                    descriptionLong = "Offrez un véritable cocon de douceur à votre nourrisson lors de ses premières sorties ou de ses siestes. Ce magnifique nid d'ange est doté d'une fermeture zippée complète et d'une capuche douillette boutonnée. Sa doublure intérieure en coton soyeux imprimé apporte confort et respirabilité à bébé.",
                    imagePath = "img_logo",
                    categories = "Accessoires, Cadeaux naissance",
                    tags = "Cadeaux naissance, Prestige",
                    stock = 8,
                    sizes = "Taille unique (0-3 mois)",
                    colors = "Beige poudré, Vert sauge",
                    material = "Extérieur 100% coton gaufré, Doublure coton, Garnissage ouate polyester",
                    entretien = "Lavage machine cycle délicat 30°C",
                    rating = 5.0f,
                    reviewCount = 6,
                    isPromo = false
                ),
                Product(
                    id = 8,
                    title = "Cape de Bain Capuche Ours Mignon",
                    sku = "CPB-OM-08",
                    priceXaf = 10500.0,
                    priceEur = 16.00,
                    descriptionShort = "Cape de bain en éponge de coton bouclette très absorbante (80x80 cm).",
                    descriptionLong = "Rendez le moment du bain merveilleux avec cette cape de bain moelleuse. Dotée d'une capuche rigolote ornée de petites oreilles d'ours brodées. Conçue dans une éponge bouclette dense ultra-absorbante qui sèche rapidement la peau fragile de votre bébé pour éviter tout coup de froid après le bain à Douala.",
                    imagePath = "img_logo",
                    categories = "Accessoires, Cadeaux naissance",
                    tags = "Toilette, Cadeaux naissance",
                    stock = 20,
                    sizes = "80 x 80 cm",
                    colors = "Écru naturel, Rose poudré, Bleu azur",
                    material = "100% Éponge de coton bouclette épaisse",
                    entretien = "Lavage machine à 40°C, sèche-linge toléré pour plus de gonflant",
                    rating = 4.8f,
                    reviewCount = 14,
                    isPromo = false
                ),
                Product(
                    id = 9,
                    title = "Coffret Cadeau Naissance Prestige",
                    sku = "CFX-PR-09",
                    priceXaf = 35000.0,
                    priceEur = 53.35,
                    descriptionShort = "Le coffret cadeau de luxe ultime comprenant 7 accessoires de naissance.",
                    descriptionLong = "Le cadeau de naissance par excellence pour faire sensation lors d'une baby shower à Douala. Ce coffret de prestige comprend : un doudou plat ourson en coton bio, un body brodé, une couverture douce, un hochet en bois de hêtre naturel, un bavoir élégant, un bonnet de naissance et une paire de petits chaussons souples tricotés. Présenté dans une boîte cadeau raffinée.",
                    imagePath = "img_logo",
                    categories = "Cadeaux naissance",
                    tags = "Prestige, Best-seller",
                    stock = 5,
                    sizes = "0-3 mois",
                    colors = "Mixte Pastel",
                    material = "Mélange coton bio, tricot doux, hochet bois hêtre naturel",
                    entretien = "Suivre les étiquettes de chaque article (lavage délicat)",
                    rating = 4.9f,
                    reviewCount = 22,
                    isPromo = false
                ),
                Product(
                    id = 10,
                    title = "Chaussons Souples Premier Pas en Cuir",
                    sku = "CHS-SP-10",
                    priceXaf = 14000.0,
                    priceEur = 21.35,
                    descriptionShort = "Chaussons souples en cuir véritable tannage végétal pour petits pieds.",
                    descriptionLong = "Des petits chaussons en cuir extra-souples qui imitent la sensation d'être pieds nus, idéal pour l'apprentissage de la marche et la motricité. Élastique doux à la cheville pour que le chausson reste bien en place sans serrer. Laisse respirer la peau sans faire transpirer les petits pieds dans le climat chaud de Douala.",
                    imagePath = "img_logo",
                    categories = "Vêtements 0-3 mois",
                    tags = "Nouveauté",
                    stock = 18,
                    sizes = "0-6 mois, 6-12 mois",
                    colors = "Beige miel, Cognac, Rose sablé",
                    material = "100% Cuir de chèvre souple tannage végétal, semelle antidérapante",
                    entretien = "Nettoyage de surface avec un chiffon humide uniquement",
                    rating = 4.5f,
                    reviewCount = 11,
                    isPromo = false
                )
            )

            for (product in initialProducts) {
                productDao.insertProduct(product)
            }
        }
    }
}
