public final class CLI {
    
    
    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            return;
        }
        String a0 = args[0];
        if ("course-reader".equals(a0)) {
            CourseReader.main();
        } else if ("weighting".equals(a0)) {
            Weighting.main();
        } else {
            System.out.println("BAD!");
        }
    }
    
}
