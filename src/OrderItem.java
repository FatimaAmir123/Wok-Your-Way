class OrderItem {
    public String name, category;
    public double price;
    public int    qty;
    public OrderItem(String name, String category, double price, int qty) {
        this.name = name; this.category = category;
        this.price = price; this.qty = qty;
    }
    public double lineTotal() { return price * qty; }
}
