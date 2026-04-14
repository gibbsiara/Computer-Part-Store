package com.example.pcbuilder;

import com.example.pcbuilder.dao.ProductDAO;
import com.example.pcbuilder.dao.UserDAO;
import com.example.pcbuilder.dao.BuildDAO;
import com.example.pcbuilder.dao.ParametersDAO;
import com.example.pcbuilder.service.AuthService;
import com.example.pcbuilder.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static final AuthService auth = new AuthService();
    private static final ProductDAO productDAO = new ProductDAO();
    private static final UserDAO userDAO = new UserDAO();
    private static final ParametersDAO parametersDAO = new ParametersDAO();
    private static final BuildDAO buildDAO = new BuildDAO();

    private static int loggedInUserId = -1;
    private static boolean isAdmin = false;

    public static void main(String[] args) {
        while (true) {
            printMenu();
            System.out.print("Twój wybór: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Podaj minimalną cenę: ");
                    double minPrice = Double.parseDouble(scanner.nextLine());
                    System.out.println("Podaj maksymalną cenę: ");
                    double maxPrice = Double.parseDouble(scanner.nextLine());
                    handleListProducts(minPrice, maxPrice);
                    break;
                case "2": handleRegister(); break;
                case "3": handleLogin(); break;
                case "4": handleLogout(); break;
                case "5":
                    if (!checkAuth()) break;
                    handleCreateBuild();
                    break;
                case "6":
                    handleListProducts(0, 10000000);
                    handleShowProductDetails();
                    break;
                case "7":
                    buildDAO.printAllBuildsWithComponents();
                    break;

                case "10": if (!checkAdmin()) break; handleListProducts(0, 10000000); handleAddProduct(); break;
                case "11": if (!checkAdmin()) break; handleListProducts(0, 10000000); handleDeleteProduct(); break;
                case "12": if (!checkAdmin()) break; handleListProducts(0, 10000000); handleUpdatePrice(); break;
                case "13": if (!checkAdmin()) break; handleListParameters();handleAddParameter(); break;
                case "14": if (!checkAdmin()) break; handlePromoteUser(); break;
                case "15": if (!checkAdmin()) break; handleListParameters(); break;
                case "16": if (!checkAdmin()) break; handleListProducts(0, 10000000); handleAddProductSpec(); break;
                case "17": if (!checkAdmin()) break; handleAddManufacturer(); break;
                case "18": if (!checkAdmin()) break; handleAddCategory(); break;
                case "19": if (!checkAdmin()) break; handleListParameters(); handleDeleteParameter(); break;
                case "0":
                    System.out.println("Do widzenia!");
                    return;
                default:
                    System.out.println("Nieprawidłowa opcja.");
            }
            System.out.println("\nNaciśnij ENTER, aby kontynuować...");
            scanner.nextLine();
        }
    }

    private static void printMenu() {
        System.out.println("\n==========================================");
        if (loggedInUserId == -1) {
            System.out.println("Status: Niezalogowany (Gość)");
        } else {
            System.out.println("Status: Zalogowany (ID: " + loggedInUserId + ")" + (isAdmin ? " [ADMIN]" : " [UŻYTKOWNIK]"));
        }
        System.out.println("------------------------------------------");
        System.out.println("1. Przeglądaj produkty");
        System.out.println("2. Zarejestruj się");
        if (loggedInUserId == -1) {
            System.out.println("3. Zaloguj się");
        }else{
            System.out.println("4. Wyloguj się");}
        System.out.println("------------------------------------------");
        System.out.println("5. KREATOR ZESTAWU PC");
        System.out.println("6. Zobacz szczegóły produktu (Specyfikacja)");
        System.out.println("7. Zobacz galerię zestawów (Wszystkie)");

        if (isAdmin) {
            System.out.println("--- PANEL ADMINISTRATORA ---");
            System.out.println("10. Dodaj nowy produkt");
            System.out.println("11. Usuń produkt");
            System.out.println("12. Zmień cenę");
            System.out.println("13. Zdefiniuj nowy parametr (np. Kolor, Socket)");
            System.out.println("14. Awansuj użytkownika na Admina");
            System.out.println("15. Lista wszystkich parametrów");
            System.out.println("16. Przypisz parametr do produktu");
            System.out.println("17. Dodaj producenta");
            System.out.println("18. Dodaj kategorię (np. CPU, GPU)");
            System.out.println("19. Usuń parametr");
        }
        System.out.println("0. Wyjście");
        System.out.println("==========================================");
    }

    private static boolean checkAuth() {
        if (loggedInUserId == -1) {
            System.out.println("ODMOWA DOSTĘPU: Ta funkcja wymaga zalogowania!");
            return false;
        }
        return true;
    }

    private static boolean checkAdmin() {
        if (!checkAuth()) return false;

        if (!isAdmin) {
            System.out.println("ODMOWA DOSTĘPU: Wymagane uprawnienia ADMINISTRATORA!");
            return false;
        }
        return true;
    }

    private static void handleLogin() {
        System.out.println("--- LOGOWANIE ---");
        System.out.print("Login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Hasło: ");
        String password = scanner.nextLine().trim();

        int userId = auth.login(login, password);
        if (userId != -1) {
            loggedInUserId = userId;
            int roleId = userDAO.getUserRole(userId);
            isAdmin = (roleId == 1);

            System.out.println("Zalogowano pomyślnie!");
        }
    }

    private static void handleLogout() {
        loggedInUserId = -1;
        isAdmin = false;
        System.out.println("Wylogowano.");
    }

    private static void handleRegister() {
        System.out.println("--- REJESTRACJA ---");
        System.out.print("Nowy login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Nowe hasło: ");
        String password = scanner.nextLine().trim();

        if (auth.register(login, password)) {
        } else {
            System.out.println("Rejestracja nieudana (login zajęty?).");
        }
    }


    private static void handleListProducts(double minPrice, double maxPrice) {
        List<Product> products = productDAO.filterByPrice(minPrice, maxPrice);
        if (products != null) {
            System.out.println("\n--- LISTA PRODUKTÓW ---");
            for (Product p : products) {
                System.out.println(p);
            }
        }
    }

    private static void handleShowProductDetails() {
        System.out.println("\n--- SZCZEGÓŁY PRODUKTU ---");
        System.out.print("Podaj ID produktu: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            productDAO.printProductSpecs(id);
        } catch (NumberFormatException e) {
            System.out.println("ID musi być liczbą.");
        }
    }

    private static void handleCreateBuild() {
        if (loggedInUserId == -1) {
            System.out.println("Błąd bezpieczeństwa: Próba dostępu bez logowania.");
            return;
        }

        System.out.println("\n--- KREATOR ZESTAWU PC ---");
        List<Integer> buildProductIds = new ArrayList<>();

        while (true) {
            System.out.println("\nAktualna ilość części w koszyku: " + buildProductIds.size());
            System.out.println("1. Dodaj produkt do zestawu");
            System.out.println("2. Zakończ i zapisz zestaw");
            System.out.println("0. Anuluj");
            System.out.print("Wybór: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                productDAO.printSimpleProductList();
                System.out.print("Podaj ID produktu do dodania: ");
                try {
                    int prodId = Integer.parseInt(scanner.nextLine());
                    if (checkCompatibility(buildProductIds, prodId)) {
                        buildProductIds.add(prodId);
                        System.out.println("Produkt dodany.");
                    } else {
                        System.out.println("Produkt odrzucony (niekompatybilny).");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Błędne ID.");
                }
            } else if (choice.equals("2")) {
                if (buildProductIds.isEmpty()) {
                    System.out.println("Zestaw jest pusty!");
                    continue;
                }
                System.out.print("Podaj nazwę zestawu: ");
                String buildName = scanner.nextLine();
                buildDAO.createBuild(loggedInUserId, buildName, buildProductIds);
                break;
            } else if (choice.equals("0")) {
                break;
            }
        }
    }

    private static int parseNumericValue(String text) {
        if (text == null) return 0;
        String numberOnly = text.replaceAll("[^0-9]", "");
        try { return Integer.parseInt(numberOnly); } catch (Exception e) { return 0; }
    }

    private static boolean checkCompatibility(List<Integer> currentBuildIds, int newProductId) {
        String newSocket = productDAO.getParameterValue(newProductId, "Socket");
        String newRamType = productDAO.getParameterValue(newProductId, "Typ pamięci");
        String newFormat = productDAO.getParameterValue(newProductId, "Format");
        int newTdp = parseNumericValue(productDAO.getParameterValue(newProductId, "TDP"));
        int newPsuPower = parseNumericValue(productDAO.getParameterValue(newProductId, "Moc"));

        int totalTDP = newTdp;
        int currentPsuPower = 0;

        for (int existingId : currentBuildIds) {
            String existingSocket = productDAO.getParameterValue(existingId, "Socket");
            String existingRamType = productDAO.getParameterValue(existingId, "Typ pamięci");
            String existingFormat = productDAO.getParameterValue(existingId, "Format");

            int existingTdp = parseNumericValue(productDAO.getParameterValue(existingId, "TDP"));
            int existingPower = parseNumericValue(productDAO.getParameterValue(existingId, "Moc"));

            totalTDP += existingTdp;
            if (existingPower > 0) currentPsuPower = existingPower;

            if (newSocket != null && existingSocket != null && !newSocket.equals(existingSocket)) {
                System.out.println("KONFLIKT: Różne sockety! (" + existingSocket + " vs " + newSocket + ")");
                return false;
            }
            if (newRamType != null && existingRamType != null && !newRamType.equals(existingRamType)) {
                System.out.println("KONFLIKT: Różne typy pamięci! (" + existingRamType + " vs " + newRamType + ")");
                return false;
            }
            if (newFormat != null && existingFormat != null) {
                if (existingFormat.equals("mATX") && newFormat.equals("ATX")) {
                    System.out.println("KONFLIKT: Płyta ATX nie wejdzie do obudowy mATX!");
                    return false;
                }
            }
        }

        int psuToCheck = (newPsuPower > 0) ? newPsuPower : currentPsuPower;
        if (psuToCheck > 0) {
            if (totalTDP > psuToCheck) {
                System.out.println("BŁĄD KRYTYCZNY: Zasilacz (" + psuToCheck + "W) jest za słaby na ten zestaw (~" + totalTDP + "W)!");
                return false;
            }
        }
        return true;
    }

    private static void handleAddProduct() {
        System.out.println("\n--- DODAWANIE PRODUKTU ---");
        System.out.print("Podaj nazwę produktu: ");
        String name = scanner.nextLine();
        System.out.print("Podaj cenę: ");
        double price = 0;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Błąd ceny."); return;
        }

        productDAO.printManufacturers();
        System.out.print("Wybierz ID producenta: ");
        int manufacturerId = 0;
        try { manufacturerId = Integer.parseInt(scanner.nextLine()); } catch(Exception e) { return; }

        productDAO.printCategories();
        System.out.print("Wybierz ID kategorii: ");
        int categoryId = 0;
        try { categoryId = Integer.parseInt(scanner.nextLine()); } catch(Exception e) { return; }

        if (productDAO.addProduct(name, price, manufacturerId, categoryId)) {
            System.out.println("Produkt dodany pomyślnie.");
        } else {
            System.out.println("Błąd bazy danych.");
        }
    }

    private static void handleDeleteProduct() {
        if (!isAdmin) return;

        System.out.println("\n--- USUWANIE PRODUKTU ---");
        productDAO.printSimpleProductList();
        System.out.print("Podaj ID produktu do usunięcia: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Czy na pewno usunąć produkt ID " + id + "? (tak/nie): ");
            if (scanner.nextLine().equalsIgnoreCase("tak")) {
                if (productDAO.deleteProduct(id)) {
                    System.out.println("Produkt usunięty.");
                } else {
                    System.out.println("Nie udało się usunąć produktu (może być używany w zestawach).");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowe ID.");
        }
    }

    private static void handleUpdatePrice() {
        if (!isAdmin) return;

        System.out.println("\n--- ZMIANA CENY ---");
        productDAO.printSimpleProductList();

        try {
            System.out.print("Podaj ID produktu: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Podaj nową cenę: ");
            double newPrice = Double.parseDouble(scanner.nextLine());

            if (productDAO.updateProductPrice(id, newPrice)) {
                System.out.println("Cena zaktualizowana.");
            } else {
                System.out.println("Nie udało się zmienić ceny (złe ID?).");
            }
        } catch (NumberFormatException e) {
            System.out.println("Błąd formatu danych.");
        }
    }

    private static void handleAddProductSpec() {
        System.out.println("\n--- PRZYPISYWANIE PARAMETRU ---");
        productDAO.printSimpleProductList();
        System.out.print("Podaj ID Produktu: ");
        int prodId = Integer.parseInt(scanner.nextLine());

        parametersDAO.printAllParameters();
        System.out.print("Podaj ID Parametru: ");
        int paramId = Integer.parseInt(scanner.nextLine());

        System.out.print("Wartość (np. 'AM4', 'ATX', '65W'): ");
        String val = scanner.nextLine().trim();

        if (productDAO.addProductSpec(prodId, paramId, val)) {
            System.out.println("Parametr przypisany!");
        } else {
            System.out.println("Błąd.");
        }
    }

    private static void handleAddManufacturer() {
        System.out.println("\n--- DODAWANIE PRODUCENTA ---");
        productDAO.printManufacturers();
        System.out.print("Nowa nazwa: ");
        String name = scanner.nextLine().trim();
        if(!name.isEmpty() && productDAO.addManufacturer(name)) {
            System.out.println("Dodano.");
        } else {
            System.out.println("Błąd.");
        }
    }

    private static void handleAddCategory() {
        System.out.println("\n--- DODAWANIE KATEGORII ---");
        productDAO.printCategories();
        System.out.print("Nowa kategoria: ");
        String name = scanner.nextLine().trim();
        if(!name.isEmpty() && productDAO.addCategory(name)) {
            System.out.println("Dodano.");
        } else {
            System.out.println("Błąd.");
        }
    }

    private static void handleListParameters() {
        parametersDAO.printAllParameters();
    }
    private static void handleDeleteParameter() {
        if (!isAdmin) return;

        System.out.println("\n--- USUWANIE PARAMETRU ---");
        System.out.print("Podaj ID parametru do usunięcia: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Czy na pewno usunąć parametr ID " + id + "? (tak/nie): ");
            if (scanner.nextLine().equalsIgnoreCase("tak")) {
                if (parametersDAO.deleteParameterDefinitions(id)) {
                    System.out.println("Parametr usunięty.");
                } else {
                    System.out.println("Nie udało się usunąć parametru.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowe ID.");
        }
    }
    private static void handleAddParameter() {
        System.out.print("Nazwa nowego parametru: ");
        String n = scanner.nextLine();
        if(parametersDAO.addParameterDefinitions(n)) System.out.println("Zdefiniowano.");
    }

    private static void handlePromoteUser() {
        System.out.print("Login użytkownika: ");
        String targetLogin = scanner.nextLine();
        if (userDAO.promoteToAdmin(targetLogin)) {
            System.out.println("Użytkownik " + targetLogin + " jest teraz Adminem.");
        } else {
            System.out.println("Błąd awansu (zły login).");
        }
    }
}